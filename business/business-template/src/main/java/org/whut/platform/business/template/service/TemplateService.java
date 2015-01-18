package org.whut.platform.business.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.platform.business.template.entity.Template;
import org.whut.platform.business.template.mapper.TemplateMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-30
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
public class TemplateService {
    @Autowired
    private TemplateMapper templateMapper;

    public void add(Template template){
        templateMapper.add(template);
    }

    public void update(Template template){
        templateMapper.update(template);
    }

    public void delete(Template template){
        templateMapper.delete(template);
    }

    public Template getById(Template template){
        return templateMapper.get(template);
    }

    public void updateResource(Template template){
       templateMapper.updateResource(template);
    }

    public List<HashMap<String,Object>> findByCondition(HashMap<String,Object> condition){
        return templateMapper.findByCondition(condition);
    }
}
