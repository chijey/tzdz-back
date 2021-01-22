package com.chijey.startup.vo;

import com.chijey.startup.security.domain.UserInfo;
import lombok.Data;

@Data
public class UserVO {
    private UserInfo userInfo;
    private String nickName;
    private Integer gender;
    private String avatarUrl;
    private String unionId;
    private String country;
    private String province;
    private String city;
    private String language;
    private String email;
    private String phone;
    private String remarks;

}
