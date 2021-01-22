package com.chijey.startup.security.rest;

import com.chijey.startup.security.service.UserInfoService;
import com.chijey.startup.param.UserInfoDTO;
import com.chijey.startup.param.VerifyDTO;
import com.chijey.startup.security.domain.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户接口")
@AllArgsConstructor
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;
    @ApiOperation("个人信息完善")
    @PostMapping("/info")
    public ResponseEntity data(@RequestBody UserInfoDTO userInfoDTO) {
        UserInfo userInfo = userInfoService.info(userInfoDTO);
        return ResponseEntity.ok(userInfo);
    }
    @ApiOperation("实名认证")
    @PostMapping("/verify")
    public ResponseEntity verify(@RequestBody VerifyDTO verifyDTO) {
        userInfoService.verify(verifyDTO);
        return ResponseEntity.ok(verifyDTO);
    }

}
