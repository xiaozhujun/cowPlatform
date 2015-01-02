package org.whut.platform.business.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.platform.business.category.entity.Category;
import org.whut.platform.business.category.mapper.CategoryMapper;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午2:50
 * To change this category use File | Settings | File Categorys.
 */
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public void add(Category category){
        categoryMapper.add(category);
    }

    public void update(Category category){
        categoryMapper.update(category);
    }

    public void delete(Category category){
        categoryMapper.delete(category);
    }

    public Category getById(Category category){
        return categoryMapper.get(category);
    }

    public HashMap<String,Object> findByCondition(HashMap<String,String> condition){
        return categoryMapper.findByCondition(condition);
    }

}
