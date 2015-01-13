package org.whut.platform.business.userTemplate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.platform.business.userTemplate.entity.UserTemplate;
import org.whut.platform.business.userTemplate.mapper.UserTemplateMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-13
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
public class UserTemplateService {
    @Autowired
    private UserTemplateMapper mapper;

    public void add(UserTemplate userTemplate){
        mapper.add(userTemplate);
    }

    public int update(UserTemplate userTemplate){
        return mapper.update(userTemplate);
    }

    public int delete(UserTemplate userTemplate){
        return mapper.delete(userTemplate);
    }

    public List<Map<String,Object>> findByCondition(Map<String,Object> condition){
        return mapper.findByCondition(condition);
    }
}
