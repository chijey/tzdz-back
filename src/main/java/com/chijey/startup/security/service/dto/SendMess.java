package com.chijey.startup.security.service.dto;

/**
 * @Author xxs
 * @Date 2020/6/27 22:10
 */
public class SendMess {
    private String toUserId;
    private String cmd;
    private Object data;


    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
