package org.whut.platform.business.userTemplate.mapper;

import org.whut.platform.business.userTemplate.entity.UserTemplate;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-13
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
public interface UserTemplateMapper extends AbstractMapper<UserTemplate> {
    public List<Map<String,Object>> findByCondition(Map<String,Object> map);
}
