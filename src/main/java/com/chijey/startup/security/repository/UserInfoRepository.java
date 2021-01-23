package com.chijey.startup.security.repository;

import com.chijey.startup.security.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> , JpaSpecificationExecutor<UserInfo> {
    UserInfo findByOpenId(String openId);

    @Modifying(clearAutomatically = true)
    @Query(value = " UPDATE UserInfo SET avatorUrl=?2 WHERE openId = ?1")
    void updateAvator(String openId, String url);


    @Modifying(clearAutomatically = true)
    @Query(value = " UPDATE UserInfo SET lifePhotos=?2 WHERE openId = ?1")
    void updateLifePhotos(String openId, String picturesPath);

}
