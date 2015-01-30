package org.whut.platform.business.resource.entity;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-28
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public enum ResourceStatus {
    NORMAL(0),DELETE(1),REMOVE(2);
    private ResourceStatus(int value){
        this.value=value;
    }
    private int value;
    public int getValue(){
        return value;
    }
}
