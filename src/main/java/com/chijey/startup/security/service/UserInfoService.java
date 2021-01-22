package com.chijey.startup.security.service;

import com.chijey.startup.param.PersonDTO;
import com.chijey.startup.param.UserInfoDTO;
import com.chijey.startup.param.VerifyDTO;
import com.chijey.startup.security.domain.UserInfo;
import com.chijey.startup.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInfoService {
    UserInfo info(UserInfoDTO userInfoDTO);

    void verify(VerifyDTO verifyDTO);

    UserInfo findByOpenId(String openId);

    Page<UserInfo> pageination(PersonDTO param, Pageable pageable);
}
