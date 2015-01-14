package org.whut.platform.business.reply.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.whut.platform.business.reply.entity.Reply;
import org.whut.platform.business.reply.entity.ReplyStatus;
import org.whut.platform.business.reply.service.ReplyService;
import org.whut.platform.business.user.security.UserContext;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-14
 * Time: 下午7:24
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/reply")
public class ReplyServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(ReplyServiceWeb.class);

    @Autowired
    private ReplyService replyService;


    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/add")
    @POST
    public String add(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Reply reply = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Reply.class);
        if(reply.getContent()==null||reply.getContent().trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        reply.setCreateTime(new Date());
        reply.setAppId(UserContext.currentUserAppId());
        reply.setUserId(UserContext.currentUserId());
        reply.setStatus(ReplyStatus.NORMAL.getValue());

        replyService.add(reply);
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/update")
    @POST
    public String update(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Reply reply = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Reply.class);
        if(reply.getId()==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        replyService.update(reply);
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/delete")
    @POST
    public String delete(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Reply reply = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Reply.class);
        if(reply.getId()==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        replyService.delete(reply);
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
        List<HashMap<String,Object>> replyList = replyService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(replyList,JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/findByTargetId")
    @POST
    public String findByTargetId(@FormParam("targetId") Long targetId){
        if(targetId==null){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,Object> condition = new HashMap<String, Object>();
        condition.put("targetId",targetId);
        List<HashMap<String,Object>> replyList = replyService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(replyList,JsonResultUtils.Code.SUCCESS);
    }
}