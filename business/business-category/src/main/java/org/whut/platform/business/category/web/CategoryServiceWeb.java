package org.whut.platform.business.category.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.whut.platform.business.category.entity.Category;
import org.whut.platform.business.category.service.CategoryService;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-31
 * Time: 下午2:50
 * To change this category use File | Settings | File Categorys.
 */
@Component
@Path("/category")
public class CategoryServiceWeb {
    private static final PlatformLogger logger = PlatformLogger.getLogger(CategoryServiceWeb.class);

    @Autowired
    private CategoryService categoryService;


    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/add")
    @POST
    public String add(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        Category category = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Category.class);
        if(category.getName()==null||category.getName().trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }

        categoryService.add(category);
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/findByCondition")
    @POST
    public String findByCondition(@FormParam("jsonString") String jsonString){
        if(jsonString==null||jsonString.trim().equals("")){
            return JsonResultUtils.getObjectResultByStringAsDefault("参数不能为空！", JsonResultUtils.Code.ERROR);
        }
        HashMap<String,String> condition = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,HashMap.class);
        HashMap<String,Object> categoryList = categoryService.findByCondition(condition);
        return JsonResultUtils.getObjectResultByStringAsDefault(categoryList,JsonResultUtils.Code.SUCCESS);
    }
}
