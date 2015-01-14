package org.whut.platform.business.reply.entity;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-14
 * Time: 下午7:30
 * To change this template use File | Settings | File Templates.
 */
public enum ReplyStatus {
    NORMAL(1),CLOSE(0);
    private ReplyStatus(int value){
        this.value=value;
    }
    private int value;
    public int getValue(){
        return value;
    }
}