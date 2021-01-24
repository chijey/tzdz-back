package com.chijey.startup.security.rest;

import com.chijey.startup.constant.Constant;
import com.chijey.startup.param.PersonDTO;
import com.chijey.startup.param.UserInfoDTO;
import com.chijey.startup.param.VerifyDTO;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.domain.UserInfo;
import com.chijey.startup.security.service.UserInfoService;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.security.utils.SecurityUtil;
import com.chijey.startup.utils.ConvertUtils;
import com.chijey.startup.utils.CosUtils;
import com.chijey.startup.utils.FileUtil;
import com.chijey.startup.utils.ResultVOUtil;
import com.chijey.startup.vo.ResultVO;
import com.chijey.startup.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/chat")
@Api(tags = "聊天接口")
@AllArgsConstructor
public class ChatController {

    @Autowired
    private UserService userService;


    @ApiOperation("获取用户")
    @GetMapping("/{openId}")
    public ResultVO verify(@PathVariable String openId) {
        List<User> users = userService.finAll();
        return ResultVOUtil.success(users);
    }





}
