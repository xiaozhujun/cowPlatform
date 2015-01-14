package org.whut.platform.business.reply.mapper;

import org.whut.platform.business.reply.entity.Reply;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-14
 * Time: 下午7:24
 * To change this template use File | Settings | File Templates.
 */
public interface ReplyMapper extends AbstractMapper<Reply> {
    public List<HashMap<String,Object>> findByCondition(HashMap<String,Object> condition);
}