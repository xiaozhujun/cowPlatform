package org.whut.platform.fundamental.util;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-29
 * Time: 上午12:59
 * To change this template use File | Settings | File Templates.
 */
public class RandomStringUtil {
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0 ; i < length; ++i){
            int number = random.nextInt(62);//[0,62)

            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
