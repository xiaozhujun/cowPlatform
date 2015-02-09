package org.whut.platform.business.userTemplate.web;

import com.mongodb.DBObject;
import org.apache.tools.ant.taskdefs.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.whut.platform.business.user.security.MyUserDetail;
import org.whut.platform.business.user.security.UserContext;
import org.whut.platform.business.userTemplate.entity.UserTemplate;
import org.whut.platform.business.userTemplate.entity.UserTemplateStatus;
import org.whut.platform.business.userTemplate.service.UserTemplateService;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.mongo.connector.MongoConnector;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;
import org.whut.platform.fundamental.util.qrcode.QRCode;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-13
 * Time: 下午3:52
 * To change this userTemplate use File | Settings | File UserTemplates.
 */
@Component
@Path("/userTemplate")
public class UserTemplateServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(UserTemplateServiceWeb.class);

    @Autowired
    private UserTemplateService userTemplateService;


    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/add")
    @POST
    public String add(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> params = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);

        if(params.get("templateNumber")==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("模板编号不能为空！", JsonResultUtils.Code.ERROR);
        }
        UserTemplate userTemplate = new UserTemplate();
        userTemplate.setTemplateNumber((String)params.get("templateNumber"));
        Object cname = params.get("cname");
        if(cname!=null){
            userTemplate.setCname((String)cname);
        }
        userTemplate.setUserId(UserContext.currentUserId());
        userTemplate.setCreateTime(new Date());
        userTemplate.setStatus(UserTemplateStatus.CLOSE.getValue());
        userTemplate.setAppId(UserContext.currentUserAppId());
        userTemplate.setNumber(UUID.randomUUID().toString());
        userTemplate.setViewCount(0);

        MongoConnector mongoConnector=new MongoConnector("userCardDB","userCardCollection");
        String mongoId = mongoConnector.insertDocument(jsonString);
        userTemplate.setMongoId(mongoId);

        userTemplateService.add(userTemplate);
        return JsonResultUtils.getObjectResultByStringAsDefault(userTemplate.getId(), JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/update")
    @POST
    public String update(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        UserTemplate userTemplate = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,UserTemplate.class);
        if(userTemplate.getId()==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        userTemplateService.update(userTemplate);
        return JsonResultUtils.getObjectResultByStringAsDefault(userTemplate.getId(), JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/delete")
    @POST
    public String delete(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        UserTemplate userTemplate = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,UserTemplate.class);
        if(userTemplate.getId()==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        userTemplateService.delete(userTemplate);
        return JsonResultUtils.getObjectResultByStringAsDefault(userTemplate.getId(), JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getByNumber")
    @POST
    public String getByNumber(@FormParam("number") String number){
        if(number==null||number.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> condition = new HashMap<String, Object>();
        condition.put("number",number);
        List<Map<String,Object>> userTemplateList = userTemplateService.findByCondition(condition);
        if(userTemplateList.size()>0){
            if(userTemplateList.get(0).get("status").equals(UserTemplateStatus.NORMAL)){
                MongoConnector mongoConnector=new MongoConnector("userCardDB","userCardCollection");
                DBObject document = mongoConnector.getDocument(userTemplateList.get(0).get("mongoId").toString());
                return JsonResultUtils.getObjectResultByStringAsDefault(document,JsonResultUtils.Code.SUCCESS);
            }else {
                return JsonResultUtils.getObjectResultByStringAsDefault("对不起，该卡片还未发布",JsonResultUtils.Code.ERROR);
            }

        }
        return JsonResultUtils.getObjectResultByStringAsDefault("卡片编号不存在！",JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/previewByNumber")
    @POST
    public String previewByNumber(@FormParam("number") String number){
        if(number==null||number.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> condition = new HashMap<String, Object>();
        condition.put("number",number);
        List<Map<String,Object>> userTemplateList = userTemplateService.findByCondition(condition);
        if(userTemplateList.size()>0){
            if (userTemplateList.get(0).get("userId").equals(UserContext.currentUserId())){
                MongoConnector mongoConnector=new MongoConnector("userCardDB","userCardCollection");
                DBObject document = mongoConnector.getDocument(userTemplateList.get(0).get("mongoId").toString());
                return JsonResultUtils.getObjectResultByStringAsDefault(document,JsonResultUtils.Code.SUCCESS);
            }else {
                return JsonResultUtils.getObjectResultByStringAsDefault("您不是卡片的所有者，不能预览模板！",JsonResultUtils.Code.SUCCESS);
            }

        }
        return JsonResultUtils.getObjectResultByStringAsDefault("卡片编号不存在！",JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/publishByNumber")
    @POST
    public String publishByNumber(@FormParam("number") String number){
        if(number==null||number.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        UserTemplate userTemplate = new UserTemplate();
        userTemplate.setUserId(UserContext.currentUserId());
        userTemplate.setNumber(number);
        userTemplate.setStatus(UserTemplateStatus.NORMAL.getValue());
        userTemplateService.updateByNumber(userTemplate);

        return JsonResultUtils.getObjectResultByStringAsDefault("发布成功！",JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/findByCondition")
    @POST
    public String findByCondition(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> condition = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);
        List<Map<String,Object>> categoryList = userTemplateService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(categoryList,JsonResultUtils.Code.ERROR);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getByUser")
    @GET
    public String getByUser(){
        MyUserDetail user = UserContext.currentUser();
        if(user==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("您还没有登录！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> condition = new HashMap<String, Object>();
        condition.put("userId",user.getId());
        List<Map<String,Object>> userTemplateList = userTemplateService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(userTemplateList,JsonResultUtils.Code.SUCCESS);
    }

    @GET
    @Path("/getQRCode/{number}")
    @Produces("image/png")
    public Response getQRCode(@PathParam("number") String number){
        try {
            if(number==null||number.trim().equals("")){
                return null;
            }
            HashMap<String,Object> condition = new HashMap<String, Object>();
            condition.put("number", number);
            List<Map<String,Object>> userTemplateList = userTemplateService.findByCondition(condition);
            if(userTemplateList.size()>0){
                if(userTemplateList.get(0).get("status").equals(UserTemplateStatus.NORMAL.getValue())){
                    Map<String,Object> card = userTemplateList.get(0);
                    String qrCode ="http://www.cseicms.com"+card.get("templateUrl")+"?cardNumber="+card.get("number");
                    BufferedImage image = QRCode.createQRCode(qrCode, 200, 200);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    byte[] imageData = baos.toByteArray();
                    return Response.ok(imageData).build();
                }
            }
        }catch (Exception e){
           logger.error(e.getMessage());
        }
        return null;
    }
}
