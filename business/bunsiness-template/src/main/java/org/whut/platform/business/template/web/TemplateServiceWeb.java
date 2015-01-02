package org.whut.platform.business.template.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.whut.platform.business.template.entity.Template;
import org.whut.platform.business.template.service.TemplateService;
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
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-30
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/template")
public class TemplateServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(TemplateServiceWeb.class);

    @Autowired
    private TemplateService templateService;


    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/add")
    @POST
    public String add(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Template template = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Template.class);
        if(template.getName()==null||template.getName().trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        templateService.add(template);
        return JsonResultUtils.getObjectResultByStringAsDefault(template.getId(), JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/findByCondition")
    @POST
    public String findByCondition(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,String> condition = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);
        HashMap<String,Object> categoryList = templateService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(categoryList,JsonResultUtils.Code.SUCCESS);
    }

    //上传模板图片
    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/uploadResource")
    @POST
    public String uploadResource(@Context HttpServletRequest request){
        if(request==null){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        String templateId = multipartRequest.getParameter("templateId");
        if(templateId==null||templateId.equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(),"模板编号不能为空！");
        }

        MultipartFile file = multipartRequest.getFile("filename");
        String filename = file.getOriginalFilename();
        String[] temp = filename.split("\\.");
        String suffix = temp[temp.length-1];

        //获得模板路径
        String templateRootPath =  FundamentalConfigProvider.get("template.resource.root.path") ;
        String templateRelativePath =  FundamentalConfigProvider.get("template.resource.relative.path") ;
        String templateUploadPath = FundamentalConfigProvider.get("template.resource.upload.path");

        Template tempTemplate = new Template();
        tempTemplate.setId(Long.parseLong(templateId));
        Template template = templateService.getById(tempTemplate);
        String templateName = template.getName();
        String templatePath =  templateUploadPath+"/"+templateName+"."+suffix;
        String templateWebPath = templateRelativePath+"/"+templateName;


        //如果文件存在则删除
        File templateFile = new File(templatePath);
        if(templateFile.exists()){
            templateFile.delete();
        }else{
            File templateDir = new File(templateUploadPath+"/");
            if(!templateDir.exists()){
                templateDir.mkdirs();
            }
        }

        //写模板图片文件到指定路径
        try {
            file.transferTo(templateFile);
            Template template1 = new Template();
            template1.setId(template.getId());
            template1.setUrl(templateWebPath);
            templateService.updateResource(template1);
            ZipUtil.upzip(templatePath,templateRootPath+"/"+templateRelativePath+"/");

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return JsonResultUtils.getObjectResultByStringAsDefault(templateWebPath,JsonResultUtils.Code.SUCCESS);
    }

}
