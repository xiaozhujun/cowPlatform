package org.whut.platform.business.userTemplate.entity;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-13
 * Time: 下午5:04
 * To change this template use File | Settings | File Templates.
 */
public enum  UserTemplateStatus{
    NORMAL(1),CLOSE(0);
    private UserTemplateStatus(int value){
        this.value=value;
    }
    private int value;
    public int getValue(){
        return value;
    }
 }
