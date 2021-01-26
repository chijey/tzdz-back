package com.chijey.startup.security.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@ApiModel(value = "消息")
@Entity
@Table(name = "message")
public class Message {
    @Id
    private String id;
    private String senderOpenId;
    private String toUserId;
    /**
     * log 消息发送
     * userList 在线用户
     */
    private String cmd;

    private String data;

    private Date createTime;
    private String contentType;

    private String imagePath;


}
