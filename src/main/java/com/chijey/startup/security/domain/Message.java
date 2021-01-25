package com.chijey.startup.security.domain;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

@Data
public class Message {
    @Id
    private String UUID;
    private String toUserId;
    /**
     * log 消息发送
     * userList 在线用户
     */
    private String cmd;

    private Object data;

    private String senderOpenId;

    private Date createTime;


}
