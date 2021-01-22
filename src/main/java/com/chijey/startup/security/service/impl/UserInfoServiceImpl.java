package com.chijey.startup.security.service.impl;

import com.chijey.startup.security.utils.SecurityUtil;
import com.chijey.startup.param.UserInfoDTO;
import com.chijey.startup.param.VerifyDTO;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.domain.UserInfo;
import com.chijey.startup.security.repository.UserInfoRepository;
import com.chijey.startup.security.service.UserInfoService;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.utils.IdCardVerify;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;


@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserService userService;
    @Override
    public UserInfo info(UserInfoDTO userInfoDTO) {
        String openId = SecurityUtil.getCurrentUserOpenId();
        User user = userService.findByOpenId(openId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        UserInfo userInfo = userInfoRepository.findByOpenId(openId);
        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setOpenId(openId);
            userInfo.setId(UUID.randomUUID().toString());
            BeanUtils.copyProperties(userInfoDTO, userInfo);
            try {
                userInfo.setBirthDate(DateUtils.parseDate(userInfoDTO.getBirthDate(),"yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("日期转换异常");
            }
            Date time = new Date();
            userInfo.setCreateTime(time);
            userInfo.setUpdTime(time);
        }else{
            BeanUtils.copyProperties(userInfoDTO, userInfo);
            Date time = new Date();
            userInfo.setUpdTime(time);
        }
        return userInfoRepository.save(userInfo);
    }

    @Override
    public void verify(VerifyDTO verifyDTO) {
        String openId = SecurityUtil.getCurrentUserOpenId();
        IdCardVerify.IdentityCardVerification(verifyDTO.getIdCard());
        UserInfo userInfo = userInfoRepository.findByOpenId(openId);
        userInfo.setIdCard(verifyDTO.getIdCard());
        userInfo.setRealName(verifyDTO.getRealName());
        userInfo.setIsRealNameValid(1);
        userInfoRepository.save(userInfo);
    }
}
