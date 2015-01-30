package org.whut.platform.business.resource.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.platform.business.resource.entity.Resource;
import org.whut.platform.business.resource.mapper.ResourceMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-28
 * Time: 下午4:01
 * To change this template use File | Settings | File Templates.
 */
public class ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    public void add(Resource resource){
        resourceMapper.add(resource);
    }

    public void update(Resource resource){
        resourceMapper.update(resource);
    }

    public void delete(Resource resource){
        resourceMapper.delete(resource);
    }

    public Resource getById(Resource resource){
        return resourceMapper.get(resource);
    }

    public List<HashMap<String,Object>> findByCondition(HashMap<String,Object> condition){
        return resourceMapper.findByCondition(condition);
    }

}
