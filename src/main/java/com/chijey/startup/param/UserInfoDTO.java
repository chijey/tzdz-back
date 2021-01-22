package com.chijey.startup.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class UserInfoDTO {
    private BigDecimal height;
    private BigDecimal weight;
    private String phone;
    private String wechatAccount;
    private String hobby;
    private String selfIntroduction;
    private String job;
    private String skill;
    private Integer isMarried;
    private String birthDate;
    private Integer constellation;//星座
    private Integer education;
    private Integer isBuyCar;
    private Integer isBuyHouse;
    private String corporation;//工作单位

}
