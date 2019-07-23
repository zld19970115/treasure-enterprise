package io.treasure.controller;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.MerchantUserDTO;

import io.treasure.enm.Common;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.service.MerchantUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户管理员
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
@RestController
@RequestMapping("/merchantuser")
@Api(tags="商户管理员")
public class MerchantUserController {
    @Autowired
    private MerchantUserService merchantUserService;
    @Autowired
    private TokenService tokenService;
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value ="1", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "10", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantUserDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus());
        PageData<MerchantUserDTO> page = merchantUserService.page(params);
        return new Result<PageData<MerchantUserDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MerchantUserDTO> get(@PathVariable("id") Long id){
        MerchantUserDTO data = merchantUserService.get(id);

        return new Result<MerchantUserDTO>().ok(data);
    }


//    @PostMapping
//    @ApiOperation("保存")
//    public Result save(@RequestBody MerchantUserDTO dto){
//            //效验数据
//            ValidatorUtils.validateEntity(dto);
//            dto.setCreateDate(new Date());
//            merchantUserService.save(dto);
//            return new Result();
//    }
//
//    @PutMapping
//    @ApiOperation("修改")
//    public Result update(@RequestBody MerchantUserDTO dto){
//        //效验数据
//        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
//        dto.setUpdateDate(new Date());
//        merchantUserService.update(dto);
//        return new Result();
//    }
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long id){
        if(id>0){
            merchantUserService.remove(id);
        }else{
            return new Result().error("删除失败！");
        }

        return new Result();
    }

    /**
     * 登陆
     * @param dto
     * @return
     */
    @PostMapping("login")
    @ApiOperation("登录")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);
        //用户登录
        Map<String, Object> map = merchantUserService.login(dto);
        return new Result().ok(map);
    }
    @Login
    @PostMapping("logout")
    @ApiOperation("退出")
    public Result logout(@ApiIgnore @RequestAttribute("userId") Long userId){
        tokenService.expireToken(userId);
        return new Result();
    }

    /**
     * 修改密码
     * @param
     * @return
     */
    @PutMapping("updatePassword")
    @ApiOperation("修改密码")
    public Result updatePassword(@RequestBody String oldPassword,String newPassword,Long id){
       String oPassword= DigestUtils.sha256Hex(oldPassword);
       String nPassword= DigestUtils.sha256Hex(newPassword);
        if(!oPassword.equals(nPassword)){
            return new Result().error("两次输入密码不一致，请重新输入！");
        }
        merchantUserService.updatePassword(nPassword,id);
        return new Result();
    }

    /**
     * 找回密码
     * @param mobile
     * @return
     */
    @PutMapping("retrievePassword")
    @ApiOperation("找回密码")
    public Result<Map<String, Object>>  retrievePassword(@RequestBody String mobile){
        if(!StringUtils.isNotBlank(mobile) || !StringUtils.isNotEmpty(mobile)){
            return new Result().error("请输入手机号码！");
        }
        MerchantUserEntity user=merchantUserService.getByMobile(mobile);
        String password=DigestUtils.sha256Hex("123456");
        user.setPassword(password);
        merchantUserService.updatePassword(password,user.getId());
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("password",123456);
        return new Result().ok(map);
    }
    /**
     * 注册
      * @param mobile
     * @param oldPassword
     * @param newPassword
     * @param weixinName
     * @param weixinUrl
     * @param openid
     * @return
     */
    @PutMapping("register")
    @ApiOperation("注册")
    public Result register(@RequestBody String mobile,String oldPassword,String newPassword,String weixinName,String weixinUrl,String openid){
        String oPassword= DigestUtils.sha256Hex(oldPassword);
        String nPassword= DigestUtils.sha256Hex(newPassword);
        if(!oPassword.equals(nPassword)){
            return new Result().error("两次输入密码不一致，请重新输入！");
        }
        MerchantUserDTO dto=new MerchantUserDTO();
        dto.setPassword(nPassword);
        dto.setMobile(mobile);
        dto.setWeixinname(weixinName);
        dto.setWeixinurl(weixinUrl);
        dto.setOpenid(openid);
        dto.setCreateDate(new Date());
        dto.setStatus(Common.STATUS_ON.getStatus());
        //效验数据
        ValidatorUtils.validateEntity(dto);
        //根据用户名判断是否已经注册过了
        MerchantUserEntity user = merchantUserService.getByMobile(mobile);
        if(null!=user){
            return new Result().error("改注册账号已存在，请换个账号重新注册!");
        }
        merchantUserService.save(dto);
        return new Result();
    }
    /**
     * 修改手机号码
     * @param mobile
     * @param  id
     * @return
     */
    @PutMapping("updateMobile")
    @ApiOperation("修改手机号")
    public Result<Map<String, Object>>  updateMobile(@RequestBody String mobile,long id){
        if(!StringUtils.isNotBlank(mobile) || !StringUtils.isNotEmpty(mobile)){
            return new Result().error("请输入手机号码！");
        }
        //根据用户名判断是否已经注册过了
        MerchantUserEntity user = merchantUserService.getByMobile(mobile);
        if(null!=user){
            return new Result().error("该手机号码已存在，请换个手机号试试！");
        }
        merchantUserService.updateMobile(mobile,id);
        return new Result();
    }
    /**
     * 帮定微信
     * @param openid
     * @param weixinName
     * @param weixinUrl
     * @param  id
     * @return
     */
    @PutMapping("updateWeixin")
    @ApiOperation("帮定微信")
    public Result<Map<String, Object>>  updateMobile(@RequestBody String openid,String weixinName,String weixinUrl,long id){
        if(!StringUtils.isNotBlank(openid) || !StringUtils.isNotEmpty(openid)){
            return new Result().error("请输入openid！");
        }
        merchantUserService.updateWeixin(openid,weixinName,weixinUrl,id);
        return new Result();
    }
}