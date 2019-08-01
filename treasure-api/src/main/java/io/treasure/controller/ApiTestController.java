/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.common.utils.Result;
import io.treasure.config.ISMSConfig;
import io.treasure.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.treasure.utils.SendSMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试接口
 *
 * @author Super 63600679@qq.com
 */
@ApiIgnore
@RestController
@RequestMapping("/api")
@Api(tags="测试接口")
public class ApiTestController {



    @Login
    @GetMapping("userInfo")
    @ApiOperation(value="获取用户信息", response=UserEntity.class)
    public Result<UserEntity> userInfo(@ApiIgnore @LoginUser UserEntity user){
        return new Result<UserEntity>().ok(user);
    }

    @Login
    @GetMapping("userId")
    @ApiOperation("获取用户ID")
    public Result<Long> userInfo(@ApiIgnore @RequestAttribute("userId") Long userId){
        return new Result<Long>().ok(userId);
    }

    @GetMapping("notToken")
    @ApiOperation("忽略Token验证测试")
    public Result<String> notToken(){

        return new Result<String>().ok("无需token也能访问。。。");
    }

}