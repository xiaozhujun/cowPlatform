package org.whut.platform.business.resource.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.whut.platform.business.resource.entity.Resource;
import org.whut.platform.business.resource.entity.ResourceStatus;
import org.whut.platform.business.resource.service.ResourceService;
import org.whut.platform.business.user.security.UserContext;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;
import org.whut.platform.fundamental.util.zip.ZipUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-28
 * Time: 下午4:01
 * To change this resource use File | Settings | File Templates.
 */
@Component
@Path("/resource")
public class ResourceServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(ResourceServiceWeb.class);

    @Autowired
    private ResourceService resourceService;

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/update")
    @POST
    public String update(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Resource resource = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Resource.class);
        if(resource.getId()==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        resourceService.update(resource);
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/delete")
    @POST
    public String delete(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Resource resource = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Resource.class);
        if(resource.getId()==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        resourceService.delete(resource);
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/findByCondition")
    @POST
    public String findByCondition(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> condition = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);
        List<HashMap<String,Object>> resourceList = resourceService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(resourceList,JsonResultUtils.Code.SUCCESS);
    }

    //上传模板图片
    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/upload")
    @POST
    public String upload(@Context HttpServletRequest request){
        if(request==null){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        String resourceId = multipartRequest.getParameter("resourceId");
        if(resourceId==null||resourceId.equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(),"模板编号不能为空！");
        }

        MultipartFile file = multipartRequest.getFile("filename");
        String filename = file.getOriginalFilename();
        String[] temp = filename.split("\\.");
        String suffix = temp[temp.length-1];

        //获得模板路径
        String resourceRootPath =  FundamentalConfigProvider.get("user.template.resource.root.path") ;
        String resourceRelativePath =  FundamentalConfigProvider.get("user.template.resource.relative.path") ;
        String resourceUploadPath = resourceRootPath+resourceRelativePath;

        String resourceName = UUID.randomUUID().toString();
        String resourcePath =  resourceUploadPath+"/"+ resourceName+"."+suffix;
        String resourceWebPath = resourceRelativePath+"/"+resourceName+"."+suffix;

        //如果文件存在则删除
        File resourceFile = new File(resourcePath);
        if(resourceFile.exists()){
            resourceFile.delete();
        }else{
            File resourceDir = new File(resourceUploadPath+"/");
            if(!resourceDir.exists()){
                resourceDir.mkdirs();
            }
        }

        Resource resource = new Resource();
        resource.setCreateTime(new Date());
        resource.setAppId(UserContext.currentUserAppId());
        resource.setUserId(UserContext.currentUserId());
        resource.setStatus(ResourceStatus.NORMAL.getValue());
        resource.setSuffix(suffix);
        resourceService.add(resource);

        return JsonResultUtils.getObjectResultByStringAsDefault(resourceWebPath,JsonResultUtils.Code.SUCCESS);
    }
}
