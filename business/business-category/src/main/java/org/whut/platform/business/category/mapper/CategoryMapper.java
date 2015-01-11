package org.whut.platform.business.category.mapper;

import org.whut.platform.business.category.entity.Category;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
public interface CategoryMapper extends AbstractMapper<Category> {
    public List<Category> findByCondition(Map<String,Object> map);
    public long getIdByName(String name);
    public String getNameById(long id);
    public List<HashMap<String,Object>> findByCondition(HashMap<String,String> condition);
}