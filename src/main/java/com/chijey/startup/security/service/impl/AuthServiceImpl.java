package com.chijey.startup.security.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chijey.startup.security.service.AuthService;
import com.chijey.startup.common.base.Result;
import com.chijey.startup.constant.Constant;
import com.chijey.startup.security.config.JwtSecurityProperties;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.security.service.dto.AuthUserDto;
import com.chijey.startup.security.utils.JwtTokenUtils;
import com.chijey.startup.utils.NetworkUtils;
import com.chijey.startup.utils.RedisUtils;
import com.chijey.startup.wechat.miniprogram.service.WxMiniApi;
import com.chijey.startup.wechat.utils.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权登录接口实现类
 *
 * @author zhuhuix
 * @date 2020-06-15
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AuthServiceImpl implements AuthService {

    @Value("${wxMini.appId}")
    private String appId;
    @Value("${wxMini.secret}")
    private String secret;

    private final JwtTokenUtils jwtTokenUtils;
    private final WxMiniApi wxMiniApi;
    private final UserService userService;
    private final JwtSecurityProperties properties;
    private final RedisUtils redisUtils;

    public AuthServiceImpl(JwtTokenUtils jwtTokenUtils, WxMiniApi wxMiniApi, UserService userService, JwtSecurityProperties properties, RedisUtils redisUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.wxMiniApi = wxMiniApi;
        this.userService = userService;
        this.properties = properties;
        this.redisUtils = redisUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AuthUserDto> login(AuthUserDto authUserDto, HttpServletRequest request) {
        // 通过缓存判断同一IP某一时间段内的登录次数是否超出限定次数
        String ip = NetworkUtils.getIp(request);
        String requestLoginIp = "request_login_".concat(ip);
        long loginCount = redisUtils.increment(requestLoginIp, 1L);
        if (loginCount == 1) {
            redisUtils.expire(requestLoginIp, Constant.REQUEST_LOGIN_LIMIT_TIME);
        }
        if (loginCount > Constant.REQUEST_LOGIN_LIMIT_COUNT) {
            log.warn("IP:[".concat(ip).concat("]已超出限定次数"));
            throw new RuntimeException("时间段内已超出限定次数,请不要频繁登录!");
        }

        Result<AuthUserDto> result = new Result<>();

        //authType=1代表是微信登录
        if (!StringUtils.isEmpty(authUserDto.getAuthType()) && authUserDto.getAuthType() == 1) {
            JSONObject jsonObject = wxMiniApi.authCode2Session(appId, secret, authUserDto.getCode());
            if (jsonObject == null) {
                throw new RuntimeException("调用微信端授权认证接口错误");
            }
            String openId = jsonObject.getString(Constant.OPEN_ID);
            String sessionKey = jsonObject.getString(Constant.SESSION_KEY);
            String unionId = jsonObject.getString(Constant.UNION_ID);
            if (StringUtils.isEmpty(openId)) {
                return result.error(jsonObject.getString(Constant.ERR_CODE), jsonObject.getString(Constant.ERR_MSG));
            }
            authUserDto.setOpenId(openId);

            //判断用户表中是否存在该用户，不存在则进行解密得到用户信息，并进行新增用户
            User resultUser = userService.findByOpenId(openId);
            if (resultUser == null) {
                String userInfo = WeChatUtil.decryptData(authUserDto.getEncryptedData(), sessionKey, authUserDto.getIv());
                if (StringUtils.isEmpty(userInfo)) {
                    throw new RuntimeException("解密用户信息错误");
                }
                User user = JSONObject.parseObject(userInfo, User.class);
                if (user == null) {
                    throw new RuntimeException("填充用户对象错误");
                }
                user.setUnionId(unionId);
                userService.create(user);
                authUserDto.setUserInfo(user);

            } else {
                authUserDto.setUserInfo(resultUser);
            }
        }

        //authType=0代表是用户名登录
        if (!StringUtils.isEmpty(authUserDto.getAuthType()) && authUserDto.getAuthType() == 0) {

            String userName = authUserDto.getUserName();
            //判断用户表中是否存在该用户，不存在则进行解密得到用户信息，并进行新增用户
            Result<User> resultUser = userService.findByUserName(userName);
            if (resultUser == null || resultUser.getSuccess() == null || !resultUser.getSuccess() || resultUser.getModule() == null) {
                throw new RuntimeException("用户名:" + userName + "无法找到对应的注册帐号");
            }
        }

        //创建token
        Map<String, Object> claims = new HashMap<>(16);
        claims.put("roles", "user");
        String token = jwtTokenUtils.createToken(authUserDto.getAuthType() == 1?authUserDto.getOpenId():authUserDto.getUserName(), claims);
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("生成token错误");
        }
        authUserDto.setToken(properties.getTokenStartWith() + token);

        // 将当前用户信息与登录时间写入Redis缓存的哈希表
        String key = authUserDto.getOpenId() != null ? authUserDto.getOpenId() : authUserDto.getUserName();
        redisUtils.hashSet(key, "id", authUserDto.getUserInfo().getId());
        redisUtils.hashSet(key, "nickName", authUserDto.getUserInfo().getNickName());
        redisUtils.hashSet(key, "avatarUrl", authUserDto.getUserInfo().getAvatarUrl());
        redisUtils.hashSet(key, "lastLoginTime", Timestamp.valueOf(LocalDateTime.now()));
        redisUtils.hashSet(key, "ip", ip);

        return result.ok(authUserDto);
    }
}
