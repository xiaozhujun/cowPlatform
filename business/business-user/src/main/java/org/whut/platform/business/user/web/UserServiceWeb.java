package org.whut.platform.business.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.whut.platform.business.app.service.AppService;
import org.whut.platform.business.user.entity.User;
import org.whut.platform.business.user.entity.UserAuthority;
import org.whut.platform.business.user.entity.UserStatus;
import org.whut.platform.business.user.security.MD5Encoder;
import org.whut.platform.business.user.security.UserContext;
import org.whut.platform.business.user.service.AuthorityService;
import org.whut.platform.business.user.service.UserAuthorityService;
import org.whut.platform.business.user.service.UserService;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.mail.MailSender;
import org.whut.platform.fundamental.util.RandomStringUtil;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-1-26
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/user")
public class UserServiceWeb {

    private static PlatformLogger logger = PlatformLogger.getLogger(UserServiceWeb.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private AppService appService;
    @Autowired
    private UserAuthorityService userAuthorityService;
    @Autowired
    private MailSender mailSender;

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/getIdByName")
    @POST
    public String getIdByName(@FormParam("name") String name){
        if (name == null) {
            return JsonResultUtils
                    .getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        long id;
        try {
            id  = userService.getIdByName(name);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
        return JsonResultUtils.getObjectResultByStringAsDefault(id,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("findByName/{name}")
    @GET
    public String findByName(@PathParam("name") String name){
        if (name == null) {
            return JsonResultUtils
                    .getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        User user;
        try {
            user = userService.findByName(name);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
        return JsonResultUtils.getObjectResultByStringAsDefault(user,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/isLogin")
    @GET
    public String  isLogin(@Context HttpServletRequest request){
        Object username = request.getSession(true).getAttribute("SPRING_SECURITY_LAST_USERNAME");
        if(username==null){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        logger.info("current user is "+username);
        User user;
        try {
            user = userService.findByEmail((String)username);
            user.setPassword("");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        return JsonResultUtils.getObjectResultByStringAsDefault(user,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/currentUser")
    @GET
    public String  currentUser(){
        String username = null;
        Object credential = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if(credential instanceof UserDetails){
            UserDetails userDetails = (UserDetails) credential;
            username = userDetails.getUsername();
        }else{
            username = (String)credential;
        }
        logger.info("current user is "+username);
        User user;
        try {
            user = userService.findByEmail(username);
            user.setPassword("");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        return JsonResultUtils.getObjectResultByStringAsDefault(user,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/currentUserName")
    @GET
    public String  currentUserName(){
        String userName = UserContext.currentUserName();
        return JsonResultUtils.getObjectResultByStringAsDefault(userName,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/currentUserId")
    @GET
    public String  currentUserId(){
        long userId = UserContext.currentUserId();
        return JsonResultUtils.getObjectResultByStringAsDefault(userId,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/currentUserAppId")
    @GET
    public String  currentUserAppId(){
        long userAppId = UserContext.currentUserAppId();
        return JsonResultUtils.getObjectResultByStringAsDefault(userAppId,JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/add")
    @POST
    public String add(@FormParam("username") String username,@FormParam("email") String email,@FormParam("password") String password){

        if(username==null||username.trim().equals("")||email==null||email.trim().equals("")||password==null||password.trim().equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "参数不能为空!");
        }
        if(userService.isEmailExist(email)){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "邮箱已存在!");
        }
        User user = new User();

        user.setPassword(password);
        user.setName(username);
        user.setEmail(email);
        user.setStatus(UserStatus.REGIST.getValue());
        user.setCreateTime(new Date());
        user.setActivateCreateTime(new Date());
        user.setActivateCode(RandomStringUtil.getRandomString(15));
        user.setAppId(1L);
        userService.add(user);
        mailSender.sendMail(email, "注册激活邮件--喜出望外", "请您激活！"+"<a href='http://localhost:8080/cardSystem/rs/user/activate/"+user.getEmail()+"/"+user.getActivateCode()+"'>激活</a>");
        return "恭喜您，注册成功！激活邮件已发到您的邮箱："+email+",请您进入邮箱激活！";

    }



    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/activate/{email}/{activateCode}")
    @GET
    public String activate(@PathParam("email") String email,@PathParam("activateCode") String activateCode){

        if(email==null||email.trim().equals("")||activateCode==null||activateCode.trim().equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "参数不能为空!");
        }
        User destUser = userService.findByEmail(email);
        if(destUser==null){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "邮箱不正确！");
        }
        if(destUser.getStatus()==UserStatus.ACTIVATE.getValue()){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "用户已激活！");
        }
        if(destUser.getStatus()==UserStatus.STOP.getValue()){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "账号被关闭！");
        }

        User user = new User();
        user.setActivateCode(activateCode);
        user.setEmail(email);
        user.setStatus(UserStatus.REGIST.getValue());
        userService.activate(user);
        return "恭喜您，用户已激活！";
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/prepareRetrievePwd")
    @POST
    public String prepareRetrievePwd(@FormParam("email") String email,@FormParam("captcha") String captcha,@Context
    HttpServletRequest request){

        if(email==null||email.trim().equals("")||captcha==null||captcha.trim().equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "参数不能为空!");
        }
        User destUser = userService.findByEmail(email);
        if(destUser==null){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "邮箱不正确！");
        }

        String sessionyanz = (String)request.getSession(true).getAttribute("yzkeyword");
        if(sessionyanz==null||!sessionyanz.equals(captcha)){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "验证码不正确！");
        }

        User user = new User();
        user.setRetrievePwdCreateTime(new Date());
        user.setRetrievePwdCode(RandomStringUtil.getRandomString(15));
        user.setEmail(email);
        userService.prepareRetrievePwd(user);
        mailSender.sendMail(email, "密码修改--喜出望外", "请您点击链接修改密码！"+"<a href='http://localhost:8080/cardSystem/resetPwd.jsp?email="+user.getEmail()+"&retrievePwdCode="+user.getRetrievePwdCode()+"'>修改密码</a>");
        return "恭喜您，密码重置邮件已发到您的邮箱："+email+",请您进入邮箱修改密码！";
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/retrievePwd")
    @POST
    public String retrievePwd(@FormParam("email") String email,@FormParam("password") String password,@FormParam("retrievePwdCode") String retrievePwdCode){

        if(email==null||email.trim().equals("")||password==null||password.trim().equals("")||retrievePwdCode==null||retrievePwdCode.trim().equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "参数不能为空!");
        }
        User destUser = userService.findByEmail(email);
        if(destUser==null){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(), "邮箱不正确！");
        }

        User user = new User();
        user.setRetrievePwdCode(retrievePwdCode);
        user.setEmail(email);
        user.setPassword(password);
        userService.retrievePwd(user);
        return "恭喜您，密码重置成功！";
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/list")
    @GET
    public String list(){

        List<User> list=userService.list();
        for( int i=0;i<list.size();i++){
            long appId=list.get(i).getAppId();
            String appName=appService.getNameById(appId);
            list.get(i).setAppName(appName);
        }

        return JsonResultUtils.getObjectResultByStringAsDefault(list, JsonResultUtils.Code.SUCCESS);
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/delete")
    @POST
    public String delete(@FormParam("jsonString") String jsonString){
        User user = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,User.class);
        String userName = user.getName();
        //删除用户角色表数据
        int userAuthorityDeleted = userAuthorityService.deleteByUserName(userName);

        //删除用户表数据
        int userDeleted = userService.delete(user);
        if((userAuthorityDeleted>0)&&(userDeleted>=0)){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
        }
        else{
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
    }

    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/update")
    @POST
    public String update(@FormParam("jsonString") String jsonString){
        User user = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,User.class);
        User User = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,User.class);
        long appId=appService.getIdByName(user.getAppName());
        user.setAppId(appId);
        User.setAppId(appId);

        String userName = user.getName();
        int userAuthorityDeleted = userAuthorityService.deleteByUserName(userName);

        if(userAuthorityDeleted>=0){
            long userId = userService.getIdByName(userName);
            //更新用户角色表
            String role = user.getRole();
            String[] roleArray = role.split(";");
            int length= roleArray.length;
            for(int i=0;i<length;i++){
                UserAuthority userAuthority = new UserAuthority();
                long authorityId= authorityService.getIdByName(roleArray[i]);
                userAuthority.setAppId(appId);
                userAuthority.setUserId(userId);
                userAuthority.setAuthorityId(authorityId);
                userAuthority.setUserName(userName);
                userAuthority.setAuthorityName(roleArray[i]);
                userAuthorityService.add(userAuthority);
            }

            //更新用户表
            int  result = userService.update(user);
            if(result>0){
                return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
            }
            else{
                return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
            }
        }
        else{
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
    }

    //上传用户图片
    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/selfUploadImage")
    @POST
    public String selfUploadImage(@Context HttpServletRequest request){
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
        String userImgRootPath =  FundamentalConfigProvider.get("template.resource.root.path") ;
        String userImgRelativePath =  FundamentalConfigProvider.get("template.resource.relative.path") ;
        String userName = UserContext.currentUserName();
        long appId = UserContext.currentUserAppId();
        String userImagePath =  userImgRootPath + userImgRelativePath+"/"+appId+"/"+userName+"."+suffix;
        String userImageWebPath = userImgRelativePath+"/"+appId+"/"+userName+"."+suffix;

        User currentUser = userService.getById(UserContext.currentUserId());

        //如果文件存在则删除
        File userImageFile = new File(userImagePath);
        String oldImagePath = currentUser.getImage();
        if(oldImagePath!=null){
            File oldImage = new File(userImgRootPath+oldImagePath);
            if(oldImage.exists()){
                oldImage.delete();
            }
        }
        if(userImageFile.exists()){
            userImageFile.delete();
        }else{
            File imageDir = new File(userImgRootPath+"/"+userImgRelativePath+"/"+appId);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }
        }

        //写用户图片文件到指定路径
        try {
            file.transferTo(userImageFile);
            User user = new User();
            user.setId(UserContext.currentUserId());
            user.setImage(userImageWebPath);
            userService.updateUserImage(user);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
        return JsonResultUtils.getObjectResultByStringAsDefault(userImageWebPath,JsonResultUtils.Code.SUCCESS);
    }

    //上传用户图片
    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/uploadImage")
    @POST
    public String uploadImage(@Context HttpServletRequest request){
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
        String userId = multipartRequest.getParameter("userId");
        if(userId==null||userId.equals("")){
            return JsonResultUtils.getCodeAndMesByString(JsonResultUtils.Code.ERROR.getCode(),"用户编号不能为空！");
        }

        User user = userService.getById(Long.parseLong(userId));
        String userName = user.getName();
        long appId = UserContext.currentUserAppId();
        String userImagePath =  userImgRootPath + userImgRelativePath+"/"+appId+"/"+userName+"."+suffix;
        String userImageWebPath = userImgRelativePath+"/"+appId+"/"+userName+"."+suffix;

        User currentUser = userService.getById(UserContext.currentUserId());

        //如果文件存在则删除
        File userImageFile = new File(userImagePath);
        String oldImagePath = user.getImage();
        if(oldImagePath!=null){
            File oldImage = new File(userImgRootPath+oldImagePath);
            if(oldImage.exists()){
                oldImage.delete();
            }
        }
        if(userImageFile.exists()){
            userImageFile.delete();
        }else{
            File imageDir = new File(userImgRootPath+"/"+userImgRelativePath+"/"+appId);
            if(!imageDir.exists()){
                imageDir.mkdirs();
            }
        }

        //写用户图片文件到指定路径
        try {
            file.transferTo(userImageFile);
            User user1 = new User();
            user1.setId(user.getId());
            user1.setImage(userImageWebPath);
            userService.updateUserImage(user1);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
        return JsonResultUtils.getObjectResultByStringAsDefault(userImageWebPath,JsonResultUtils.Code.SUCCESS);
    }


    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/keepAlive")
    @POST
    public String keepAlive(){
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
}
