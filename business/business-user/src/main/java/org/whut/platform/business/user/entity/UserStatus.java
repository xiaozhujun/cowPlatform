package org.whut.platform.business.user.entity;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-26
 * Time: 下午10:34
 * To change this template use File | Settings | File Templates.
 */
public enum UserStatus {
    REGIST(0),ACTIVATE(1),STOP(2);

    private Integer value;
    private UserStatus(Integer value){
        this.value=value;
    }

    public Integer getValue(){
        return this.value;
    }

}
