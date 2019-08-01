package io.treasure.controller;


import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;

import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.ClientUserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.dto.LoginDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.ClientUserService;

import io.treasure.service.TokenService;
import io.treasure.utils.SendSMSUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Map;


/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@RestController
@RequestMapping("/api")
@Api(tags="用户信息")
public class ApiClientUserController {
    @Autowired
    private ClientUserService clientUserService;
    @Autowired
    private SMSConfig smsConfig;
    @Autowired
    private TokenService tokenService;

    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<ClientUserDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ClientUserDTO> page = clientUserService.page(params);

        return new Result<PageData<ClientUserDTO>>().ok(page);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<ClientUserDTO> get(@PathVariable("id") Long id){
        ClientUserDTO data = clientUserService.get(id);

        return new Result<ClientUserDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody ClientUserDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        clientUserService.save(dto);

        return new Result();
    }

    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody ClientUserDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        clientUserService.update(dto);

        return new Result();
    }

    @Login
    @DeleteMapping
    @ApiOperation("用户删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="用户编码",required=true,paramType="query")
    })
    public Result delete(Long id){
        //效验数据
        AssertUtils.isNull(id,"id");

        ClientUserDTO clientUserDTO=clientUserService.get(id);
        clientUserDTO.setStatus(9);
        clientUserService.update(clientUserDTO);
        return new Result();
    }

    @GetMapping("isRegister")
    @ApiOperation("验证手机是否注册:true-已注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name="tel",value="手机号",required=true,paramType="query")
    })
    public Result isRegister(String tel){
        AssertUtils.isBlank(tel,"tel");
        boolean b=clientUserService.isRegister(tel);
        return new Result().ok(b);
    }

    @GetMapping("userRegisterCode")
    @ApiOperation("用户注册验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="tel",value="手机号",required=true,paramType="query")
    })
    public Result register(HttpServletRequest request,String tel){
        boolean bool= SendSMSUtil.sendCodeForRegister(tel,request,smsConfig);
        return new Result().ok(bool);
    }

    @GetMapping("userRegisterVerifyCode")
    @ApiOperation("用户注册验证码校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name="tel",value="手机号",required=true,paramType="query"),
            @ApiImplicitParam(name="code",value="验证码",required=true,paramType="query")
    })
    public Result verifyCode(HttpServletRequest request,String tel,String code){
        Result bool=SendSMSUtil.verifyCode(tel,request,code);
        return bool;
    }

    @PostMapping("userRegister")
    @ApiOperation("用户注册")
    public Result userRegister(@RequestBody ClientUserDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);

        ClientUserEntity user = new ClientUserEntity();
        user.setMobile(dto.getMobile());
        user.setUsername(dto.getMobile());
        user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        user.setCreateDate(new Date());
        clientUserService.insert(user);
        return new Result();
    }

    @PostMapping("userLogin")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);

        //用户登录
        Map<String, Object> map = clientUserService.login(dto);

        return new Result().ok(map);
    }

    @Login
    @PostMapping("userLogout")
    @ApiOperation("用户退出")
    public Result logout(@ApiIgnore @RequestAttribute("userId") Long userId){
        tokenService.expireToken(userId);
        return new Result();
    }

}