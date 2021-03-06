package com.chijey.startup.security.service.dto;

import lombok.Data;

/**
 * @Author xxs
 * @Date 2020/6/27 22:10
 */
@Data
public class SendMess {
    private String toUserId;
    /**
     * log 消息发送
     * userList 在线用户
     */
    private String cmd;

    private Object data;

    private String senderOpenId;


}
