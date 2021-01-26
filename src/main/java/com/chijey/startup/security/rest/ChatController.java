package com.chijey.startup.security.rest;

import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.service.MessageService;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.security.utils.SecurityUtil;
import com.chijey.startup.utils.DateUtils;
import com.chijey.startup.utils.ResultVOUtil;
import com.chijey.startup.vo.ResultVO;
import com.chijey.startup.vo.UserChatVO;
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
    public ResultVO chatWith(@PathVariable String openId) {
        List<Message> messages = messageService.findChatWith(openId);
        List<UserChatVO> userChatVOS = new ArrayList<>();
        for(Message msg: messages){
            UserChatVO userChatVO = new UserChatVO();
            String toUserId = msg.getToUserId();
            String senderOpenId = msg.getSenderOpenId();
            if(toUserId.equals(openId)){
                userChatVO.setChatWithId(senderOpenId);
                User chatUser = userService.findByOpenId(senderOpenId);
                userChatVO.setAvatarUrl(chatUser.getAvatarUrl());
                userChatVO.setNickName(chatUser.getNickName());
            }else if(senderOpenId.equals(openId)){
                userChatVO.setChatWithId(toUserId);
                User chatUser = userService.findByOpenId(toUserId);
                userChatVO.setAvatarUrl(chatUser.getAvatarUrl());
                userChatVO.setNickName(chatUser.getNickName());
            }
            userChatVO.setContent(msg.getData());
            userChatVO.setCreateTime(DateUtils.formatTimeStamp(msg.getCreateTime().getTime(),"yyyy-MM-dd mm:hh:ss"));
            userChatVOS.add(userChatVO);
        }
        return ResultVOUtil.success(userChatVOS);
    }

    @ApiOperation("获取聊天信息")
    @GetMapping("/loadMessage")
    public ResultVO loadMSG(@RequestParam String toOpenId) {
        String openId = SecurityUtil.getCurrentUserOpenId();
        List<Message> messages = messageService.findByToUserIdAndSenderId(toOpenId,openId);
        return ResultVOUtil.success(messages);
    }


}
