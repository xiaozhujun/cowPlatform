package org.whut.platform.business.resource.mapper;

import org.whut.platform.business.resource.entity.Resource;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-28
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceMapper extends AbstractMapper<Resource> {
    public List<HashMap<String,Object>> findByCondition(HashMap<String,Object> condition);
}
