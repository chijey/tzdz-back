package com.chijey.startup.security.repository;

import com.chijey.startup.security.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    UserInfo findByOpenId(String openId);
}
