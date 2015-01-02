package org.whut.platform.fundamental.mail;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-28
 * Time: 下午8:35
 * To change this template use File | Settings | File Templates.
 */


import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * 简单邮件模板生成引擎实现
 *
 * @author quanxiwei
 *
 */
public class MailContentEngine  {

    public String generateContent(String template, Map<String, String> pros) {
        for (Map.Entry<String, String> entry : pros.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            template = StringUtils.replace(template, wrapper(key), value);
        }
        return template;
    }

    /**
     * 所有的占位符采用${stringholder}的形式
     *
     * @param key
     * @return
     */
    private String wrapper(String key) {
        return new StringBuilder("${").append(key).append("}").toString();
    }
}

