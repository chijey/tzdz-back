package com.chijey.startup.security.service;

import com.chijey.startup.param.UserInfoDTO;
import com.chijey.startup.param.VerifyDTO;
import com.chijey.startup.security.domain.UserInfo;

public interface UserInfoService {
    UserInfo info(UserInfoDTO userInfoDTO);

    void verify(VerifyDTO verifyDTO);
}
