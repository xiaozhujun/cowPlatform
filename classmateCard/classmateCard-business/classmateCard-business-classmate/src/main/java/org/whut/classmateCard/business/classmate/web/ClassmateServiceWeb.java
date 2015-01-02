package org.whut.classmateCard.business.classmate.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.whut.classmateCard.business.classmate.entity.Classmate;
import org.whut.classmateCard.business.classmate.service.ClassmateService;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午11:18
 * To change this classmate use File | Settings | File Classmates.
 */
@Component
@Path("/classmate")
public class ClassmateServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(ClassmateServiceWeb.class);

    @Autowired
    private ClassmateService classmateService;


    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/add")
    @POST
    public String add(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Classmate classmate = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Classmate.class);
        if(classmate.getName()==null||classmate.getName().trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        classmateService.add(classmate);
        return JsonResultUtils.getObjectResultByStringAsDefault(classmate.getId(), JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/findByCondition")
    @POST
    public String findByCondition(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,String> condition = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);
        List<HashMap<String,Object>> categoryList = classmateService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(categoryList,JsonResultUtils.Code.SUCCESS);
    }
}
