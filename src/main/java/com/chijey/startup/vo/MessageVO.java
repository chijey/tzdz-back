package com.chijey.startup.vo;

import lombok.Data;

import java.util.Date;
@Data
public class MessageVO {
    private String content;
    private Date time;
    private String speakerName;
    private String contentType;
    private String senderId;
    private String receiverId;



}
