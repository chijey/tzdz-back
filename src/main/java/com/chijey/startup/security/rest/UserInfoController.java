package com.chijey.startup.security.rest;

import com.chijey.startup.constant.Constant;
import com.chijey.startup.param.PersonDTO;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.service.UserInfoService;
import com.chijey.startup.param.UserInfoDTO;
import com.chijey.startup.param.VerifyDTO;
import com.chijey.startup.security.domain.UserInfo;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.security.utils.SecurityUtil;
import com.chijey.startup.utils.ConvertUtils;
import com.chijey.startup.utils.CosUtils;
import com.chijey.startup.utils.FileUtil;
import com.chijey.startup.vo.UserVO;
import com.chijey.startup.wechat.miniprogram.service.WxMiniCrm;
import com.qcloud.cos.model.PutObjectResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户接口")
@AllArgsConstructor
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserService userService;
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

    @ApiOperation("获取用户")
    @GetMapping("/{openId}")
    public ResponseEntity verify(@PathVariable String openId) {
        UserInfo userInfo = userInfoService.findByOpenId(openId);
        User user = userService.findByOpenId(openId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        userVO.setUserInfo(userInfo);
        return ResponseEntity.ok(userVO);
    }

    @ApiOperation("获取用户")
    @PostMapping("/pagination")
    public ResponseEntity pagination(@RequestBody PersonDTO param, @RequestParam("page") Integer page, @RequestParam("size") Integer size,
                                     @RequestParam(value = "sort",defaultValue = "createTime: DESC") String sorts) {
        Pageable pageable = ConvertUtils.pagingConvert(page,size,sorts);
        Page<UserInfo> userspage = userInfoService.pageination(param,pageable);
        return ResponseEntity.ok(userspage);
    }


    @ApiOperation(value = "微信头像端上传文件")
    @PostMapping(value = "/avator/fileUpload")
    public ResponseEntity fileUpload(HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = req.getFile("file");
        String openId = SecurityUtil.getCurrentUserOpenId();
        String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")+1);
        String key = "/avator/"+openId+"."+suffix;
        CosUtils.uploadFile(key, FileUtil.multipartFileToFile(multipartFile));
        String url = Constant.COS_BUCKET_SERVER +key;
        userInfoService.update(openId,url);
        return ResponseEntity.ok(url);

    }
    @ApiOperation(value = "微信生活照上传文件")
    @PostMapping(value = "/life/fileUpload")
    public ResponseEntity lifefileUpload(MultipartFile [] files) throws Exception {
        List<String> picturesPath = new ArrayList<>();
        String openId = SecurityUtil.getCurrentUserOpenId();
        for(MultipartFile file:files){
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            String picId = UUID.randomUUID().toString();
            String key = "/avator/"+openId+"/"+picId+"."+suffix;
            String url = Constant.COS_BUCKET_SERVER +key;
            CosUtils.uploadFile(key, FileUtil.multipartFileToFile(file));
            picturesPath.add(url);
        }
        userInfoService.updateLifePhotos(openId,picturesPath);
        return ResponseEntity.ok(picturesPath);

    }



}
