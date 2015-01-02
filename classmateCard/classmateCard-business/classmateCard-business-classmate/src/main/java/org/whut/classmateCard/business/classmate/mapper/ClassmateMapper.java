package org.whut.classmateCard.business.classmate.mapper;

import org.whut.classmateCard.business.classmate.entity.Classmate;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午11:18
 * To change this template use File | Settings | File Templates.
 */
public interface ClassmateMapper extends AbstractMapper<Classmate> {
    public List<HashMap<String,Object>> findByCondition(HashMap<String,String> condition);
}
