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
import org.whut.platform.business.user.entity.User;
import org.whut.platform.business.user.security.UserContext;
import org.whut.platform.business.user.service.UserService;
import org.whut.platform.fundamental.bcs.BcsProxy;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.util.icon.IconUtil;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-2-12
 * Time: 上午9:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/resourceIcon")
public class ResourceIconServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(ResourceIconServiceWeb.class);

    @Autowired
    private ResourceService resourceService;

    //上传原始图片
    @Produces( MediaType.TEXT_HTML + ";charset=UTF-8")
    @Path("/uploadImage")
    @POST
    public String uploadImage(@Context HttpServletRequest request,@Context HttpServletResponse response){
//        response.setContentType("text/html");
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");

        if(request==null){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        MultipartFile file = multipartRequest.getFile("filename");

        String resourceType = multipartRequest.getParameter("resourceType");
        if(resourceType!=null&&!resourceType.trim().equals("")){
            resourceType+="/";
        }else{
            resourceType="";
        }

        String filename = file.getOriginalFilename();
        String[] temp = filename.split("\\.");
        String suffix = temp[temp.length-1];

        //获得用户图片路径
        String resourceRootPath =  FundamentalConfigProvider.get("user.template.resource.root.path") ;
        String resourceRelativePath =  FundamentalConfigProvider.get("user.template.resource.relative.path") +"/temp/"+resourceType;

        String imageName = UUID.randomUUID().toString();
        String imageAbsolutePath =  resourceRootPath + resourceRelativePath+imageName+"."+suffix;
        String imageResourceWebPath = resourceRelativePath+imageName+"."+suffix;


        //如果文件存在则删除
        File userImageFile = new File(imageAbsolutePath);

        if(userImageFile.exists()){
            userImageFile.delete();
        }else{
            File imageDir = new File(resourceRootPath+"/"+resourceRelativePath);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }
        }

        String resourceImageLocalPath = imageResourceWebPath;
        //写用户图片文件到指定路径
        try {
            file.transferTo(userImageFile);

            if(FundamentalConfigProvider.isBae()){
                imageResourceWebPath = BcsProxy.uploadFile(userImageFile,imageResourceWebPath);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        HashMap map = new HashMap();
        map.put("imageLocalPath",resourceImageLocalPath);
        map.put("imageWebPath",imageResourceWebPath);

        // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
        return JsonResultUtils.getObjectResultByStringAsDefault(map,JsonResultUtils.Code.SUCCESS);
    }

    //上传原始图片
    @Produces( MediaType.TEXT_HTML + ";charset=UTF-8")
    @Path("/cutImage")
    @POST
    public String cutImage(@FormParam("jsonString") String jsonString,@Context HttpServletRequest request,@Context HttpServletResponse response){
//        response.setContentType("text/html");
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");

        String userIconImagePath = "";
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！",JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> param = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);

        // 用户经过剪辑后的图片的大小
        Integer x = (Integer)param.get("x");
        Integer y = (Integer)param.get("y");
        Integer w = (Integer)param.get("w");
        Integer h = (Integer)param.get("h");

        String resourceType = (String)param.get("resourceType");
        if(resourceType!=null&&!resourceType.trim().equals("")){
            resourceType+="/";
        }else{
            resourceType="";
        }

        //获取原显示图片路径
        String originalImage = (String)param.get("originalImage");

        String resourceRootPath =  FundamentalConfigProvider.get("user.template.resource.root.path") ;
        String resourceRelativePath =  FundamentalConfigProvider.get("user.template.resource.relative.path") +"/"+resourceType;


        String[] temp = originalImage.split("\\.");
        String suffix = temp[temp.length-1];

        String iconName = UUID.randomUUID().toString();
        String resourceIconWebPath = resourceRelativePath+iconName+"."+suffix;


        //组装图片真实名称
        String createImgPath = resourceRootPath + resourceIconWebPath;

        //之前上传的图片路径
        String webAppPath = resourceRootPath + originalImage;

        logger.info("原图片路径: " + webAppPath + ",新图片路径: " + createImgPath);

        //如果文件存在则删除
        File userIconFile = new File(createImgPath);
        if(userIconFile.exists()){
            userIconFile.delete();
        }else{
            File imageDir = new File(resourceRootPath+"/"+resourceRelativePath);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }
        }

        //进行剪切图片操作
        IconUtil.abscut(webAppPath, createImgPath, x, y, w, h);

        File f = new File(createImgPath);
        if(f.exists()){
            logger.info("剪切图片大小: "+w+"*"+h+"图片成功!");

            if(FundamentalConfigProvider.isBae()){
               resourceIconWebPath = BcsProxy.uploadFile(f,resourceIconWebPath);
               f.delete();
            }

            Resource resource = new Resource();
            resource.setCreateTime(new Date());
            resource.setStatus(ResourceStatus.NORMAL.getValue());
            resource.setFile(createImgPath);
            resource.setUrl(resourceIconWebPath);
            resource.setSuffix(suffix);
            resourceService.add(resource);

            HashMap<String,Object> map =new HashMap<String, Object>();
            map.put("resourcePath",resourceIconWebPath);
            map.put("resourceId",resource.getId());
            return JsonResultUtils.getObjectResultByStringAsDefault(map,JsonResultUtils.Code.SUCCESS);
        }

        return JsonResultUtils.getObjectResultByStringAsDefault("头像上传失败！",JsonResultUtils.Code.ERROR);

    }

}
