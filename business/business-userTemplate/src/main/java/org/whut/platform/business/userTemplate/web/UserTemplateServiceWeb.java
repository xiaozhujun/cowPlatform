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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
        userTemplate.setUserId(UserContext.currentUserId());
        userTemplate.setCreateTime(new Date());
        userTemplate.setStatus(UserTemplateStatus.NORMAL.getValue());
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
            MongoConnector mongoConnector=new MongoConnector("userCardDB","userCardCollection");
            DBObject document = mongoConnector.getDocument(userTemplateList.get(0).get("mongoId").toString());
            return JsonResultUtils.getObjectResultByStringAsDefault(document,JsonResultUtils.Code.SUCCESS);
        }
        return JsonResultUtils.getObjectResultByStringAsDefault("操作错误",JsonResultUtils.Code.SUCCESS);
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

}
