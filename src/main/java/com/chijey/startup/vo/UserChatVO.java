package com.chijey.startup.vo;

import lombok.Data;

@Data
public class UserChatVO {
    private String chatWithId;
    private String content;
    private String createTime;
    private String nickName;
    private String avatarUrl;
}
