package org.whut.platform.business.template.mapper;

import org.whut.platform.business.template.entity.Template;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-30
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
public interface TemplateMapper  extends AbstractMapper<Template> {
    public List<Template> findByCondition(Map<String,Object> map);
    public long getIdByName(String name);
    public String getNameById(long id);
    public void updateResource(Template template);
    public List<HashMap<String,Object>> findByCondition(HashMap<String,String> condition);
}