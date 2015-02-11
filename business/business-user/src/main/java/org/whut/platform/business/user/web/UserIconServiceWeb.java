package org.whut.platform.business.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.whut.platform.business.user.entity.User;
import org.whut.platform.business.user.icon.ImageCut;
import org.whut.platform.business.user.security.UserContext;
import org.whut.platform.business.user.service.UserService;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-2-9
 * Time: 下午6:22
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/userIcon")
public class UserIconServiceWeb {

    private static final PlatformLogger logger = PlatformLogger.getLogger(UserIconServiceWeb.class);

    @Autowired
    private UserService userService;

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
        String filename = file.getOriginalFilename();
        String[] temp = filename.split("\\.");
        String suffix = temp[temp.length-1];

        //获得用户图片路径
        String userImgRootPath =  FundamentalConfigProvider.get("user.img.root.path") ;
        String userImgRelativePath =  FundamentalConfigProvider.get("user.img.relative.path") ;
        String userId = UserContext.currentUserId().toString();


        long appId = UserContext.currentUserAppId();
        String imageName = UUID.randomUUID().toString();
        String userImagePath =  userImgRootPath + userImgRelativePath+"/temp/"+appId+"/"+imageName+"."+suffix;
        String userImageWebPath = userImgRelativePath+"/temp/"+appId+"/"+imageName+"."+suffix;


        //如果文件存在则删除
        File userImageFile = new File(userImagePath);

        if(userImageFile.exists()){
            userImageFile.delete();
        }else{
            File imageDir = new File(userImgRootPath+"/"+userImgRelativePath+"/temp/"+appId);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }
        }

        //写用户图片文件到指定路径
        try {
            file.transferTo(userImageFile);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
        return JsonResultUtils.getObjectResultByStringAsDefault(userImageWebPath,JsonResultUtils.Code.SUCCESS);
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

        //获取原显示图片路径
        String originalImage = (String)param.get("originalImage");

        //获得用户图片路径
        String userImgRootPath =  FundamentalConfigProvider.get("user.img.root.path") ;
        String userImgRelativePath =  FundamentalConfigProvider.get("user.img.relative.path") ;

        String[] temp = originalImage.split("\\.");
        String suffix = temp[temp.length-1];

        long appId = UserContext.currentUserAppId();
        String iconName = UUID.randomUUID().toString();
        String userIconWebPath = userImgRelativePath+"/"+appId+"/"+iconName+"."+suffix;


        //组装图片真实名称
        String createImgPath = userImgRootPath + userIconWebPath;

        //之前上传的图片路径
        String webAppPath = userImgRootPath + originalImage;

        logger.info("原图片路径: " + webAppPath + ",新图片路径: " + createImgPath);

        //如果文件存在则删除
        File userIconFile = new File(createImgPath);
        if(userIconFile.exists()){
            userIconFile.delete();
        }else{
            File imageDir = new File(userImgRootPath+"/"+userImgRelativePath+"/"+appId);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }
        }

        //进行剪切图片操作
        ImageCut.abscut(webAppPath, createImgPath, x, y, w, h);

        User oldUser = userService.getById(UserContext.currentUserId());
        String userOldIcon = oldUser.getImage();
        if(userOldIcon!=null&&!userOldIcon.trim().equals("")){
            File oldIconFile = new File(userImgRootPath+userOldIcon);
            if(oldIconFile.exists()){
                oldIconFile.delete();
            }
        }

        File f = new File(createImgPath);
        if(f.exists()){
            logger.info("剪切图片大小: "+w+"*"+h+"图片成功!");
            User user = new User();
            user.setId(UserContext.currentUserId());
            user.setImage(userIconWebPath);
            userService.updateUserImage(user);

            // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
            return JsonResultUtils.getObjectResultByStringAsDefault(userIconWebPath,JsonResultUtils.Code.SUCCESS);
        }

        return JsonResultUtils.getObjectResultByStringAsDefault("头像上传失败！",JsonResultUtils.Code.ERROR);

    }


}
