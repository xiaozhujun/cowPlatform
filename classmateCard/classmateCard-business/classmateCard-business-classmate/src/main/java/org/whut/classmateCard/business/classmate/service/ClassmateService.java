package org.whut.classmateCard.business.classmate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.classmateCard.business.classmate.entity.Classmate;
import org.whut.classmateCard.business.classmate.mapper.ClassmateMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午11:18
 * To change this template use File | Settings | File Templates.
 */
public class ClassmateService {

    @Autowired
    private ClassmateMapper classmateMapper;

    public void add(Classmate classmate)
    {
        classmateMapper.add(classmate);
    }
    public void update(Classmate classmate){
        classmateMapper.update(classmate);
    }
    public void delete(Classmate classmate){
        classmateMapper.delete(classmate );
    }

    public List<HashMap<String,Object>> findByCondition(HashMap<String,String> condition){
        return classmateMapper.findByCondition(condition);
    }
}
