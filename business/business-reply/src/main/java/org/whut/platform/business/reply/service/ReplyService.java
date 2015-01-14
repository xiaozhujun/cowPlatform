package org.whut.platform.business.reply.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.platform.business.reply.entity.Reply;
import org.whut.platform.business.reply.mapper.ReplyMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-14
 * Time: 下午7:24
 * To change this template use File | Settings | File Templates.
 */
public class ReplyService {
    @Autowired
    private ReplyMapper replyMapper;

    public void add(Reply reply){
        replyMapper.add(reply);
    }

    public void update(Reply reply){
        replyMapper.update(reply);
    }

    public void delete(Reply reply){
        replyMapper.delete(reply);
    }

    public Reply getById(Reply reply){
        return replyMapper.get(reply);
    }

    public List<HashMap<String,Object>> findByCondition(HashMap<String,Object> condition){
        return replyMapper.findByCondition(condition);
    }

}