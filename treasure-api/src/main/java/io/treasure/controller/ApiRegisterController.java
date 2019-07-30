/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.controller;

import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.entity.UserEntity;
import io.treasure.dto.RegisterDTO;
import io.treasure.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.utils.SendSMSUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 注册接口
 *
 * @author Super 63600679@qq.com
 */
@RestController
@RequestMapping("/api")
@Api(tags="注册接口")
public class ApiRegisterController {
    @Autowired
    private UserService userService;
    @Autowired
    private SMSConfig smsConfig;

    @PostMapping("register")
    @ApiOperation("注册")
    public Result register(@RequestBody RegisterDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);

        UserEntity user = new UserEntity();
        user.setMobile(dto.getMobile());
        user.setUsername(dto.getMobile());
        user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        user.setCreateDate(new Date());
        userService.insert(user);

        return new Result();
    }
    @GetMapping("code")
    @ApiOperation("验证码")
    public Result register(HttpServletRequest request){
        boolean bool=SendSMSUtil.sendCodeForRegister("13694600620",request,smsConfig);
        return new Result().ok(bool);
    }

    @GetMapping("verifyCode")
    @ApiOperation("验证")
    public Result verifyCode(HttpServletRequest request,String code){
        Result bool=SendSMSUtil.verifyCode("13694600620",request,code);
        return bool;
    }
}