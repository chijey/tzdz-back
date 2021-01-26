package com.chijey.startup.security.rest;

import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.service.MessageService;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.security.utils.SecurityUtil;
import com.chijey.startup.utils.ResultVOUtil;
import com.chijey.startup.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/chat")
@Api(tags = "聊天接口")
@AllArgsConstructor
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    @ApiOperation("获取用户")
    @GetMapping("/{openId}")
    public ResultVO verify(@PathVariable String openId) {
        List<User> users = userService.finAll();
        return ResultVOUtil.success(users);
    }

    @ApiOperation("获取聊天信息")
    @GetMapping("/loadMessage")
    public ResultVO loadMSG(@RequestParam String toOpenId) {
        String openId = SecurityUtil.getCurrentUserOpenId();
        List<Message> messages = messageService.findByToUserIdAndSenderId(toOpenId,openId);
        return ResultVOUtil.success(messages);
    }


}
