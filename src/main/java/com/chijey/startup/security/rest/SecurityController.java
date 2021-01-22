package com.chijey.startup.security.rest;

import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * api授权
 *
 * @author zhuhuix
 * @date 2020-03-30
 */
@Slf4j
@RestController
@RequestMapping("/api/security")
@Api(tags = "安全接口")
@AllArgsConstructor
public class SecurityController {
    @Autowired
    private UserService userService;

    @ApiOperation("获取身份")
    @GetMapping(value = "/getUserInfo/{openId}")
    public ResponseEntity getUserInfo(@PathVariable String openId) {
        return ResponseEntity.ok(userService.findByOpenId(openId));
    }



    @ApiOperation("更新身份")
    @PostMapping(value = "/saveUserInfo/{openId}")
    public ResponseEntity saveUserInfo(@RequestBody User user, @PathVariable String openId) {
        return ResponseEntity.ok(userService.update(user, openId));

    }

}
