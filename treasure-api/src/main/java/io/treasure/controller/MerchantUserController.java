package io.treasure.controller;
import io.swagger.annotations.*;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;

import io.treasure.dto.MerchantUserRegisterDTO;
import io.treasure.enm.Common;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.service.MerchantUserService;

import io.treasure.service.TokenService;
import io.treasure.utils.SendSMSUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private SMSConfig smsConfig;

    @CrossOrigin
    @Login
    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value ="1", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "10", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "weixinname" ,value = "微信名称", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="mobile", value = "手机号码", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",required = true,paramType = "query", dataType="Long")
    })
    public Result<PageData<MerchantUserDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantUserDTO> page = merchantUserService.page(params);
        return new Result<PageData<MerchantUserDTO>>().ok(page);
    }
    @Login
    @GetMapping("{id}")
    @ApiOperation("详细信息")
    public Result<MerchantUserDTO> get(@PathVariable("id") Long id){
        MerchantUserDTO data = merchantUserService.get(id);

        return new Result<MerchantUserDTO>().ok(data);
    }


//    @PostMapping("save")
//    @ApiOperation("创建下级会员")
//    public Result save(@RequestBody MerchantUserDTO dto){
//            //效验数据
//            ValidatorUtils.validateEntity(dto,AddGroup.class);
//            //根据用户名判断是否已经注册过了
//            MerchantUserEntity user = merchantUserService.getByMobile(dto.getMobile());
//            if(null!=user){
//                return new Result().error("改注册账号已存在，请换个账号重新注册!");
//            }
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
    @Login
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@PathVariable("id") Long id){
        merchantUserService.remove(id);
        return new Result();
    }

    /**
     * 登陆
     * @param dto
     * @return
     */
    @CrossOrigin
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
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",value="编号",required=true,paramType="query", dataType="long")
    })
    public Result logout(Long userId){
        tokenService.expireToken(userId);
        return new Result();
    }

    /**
     * 修改密码
     * @param
     * @return
     */
    @CrossOrigin
    @Login
    @PutMapping("updatePassword")
    @ApiOperation("修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="oldPassword",value="第一次输入的密码",required=true,paramType="query", dataType="String"),
            @ApiImplicitParam(name="newPassword",value="第二次输入的密码",required=true,paramType="query", dataType="String"),
            @ApiImplicitParam(name="id",value="会员编号",required = true,paramType = "query", dataType="long")
    })
    public Result updatePassword(HttpServletRequest request,String oldPassword,String newPassword,Long id){
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
    @CrossOrigin
    @Login
    @PutMapping("retrievePassword")
    @ApiOperation("找回密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value="手机号码",required=true,paramType="query", dataType="String"),
            @ApiImplicitParam(name="id",value="会员编号",required = true,paramType = "query", dataType="Long")
    })
    public Result<Map<String, Object>>  retrievePassword(String mobile,Long id){
       MerchantUserDTO user= merchantUserService.get(id);
       if(null!=user){
           if(!user.getMobile().equals(mobile)){
               return new Result().error("手机号输入错误！");
           }
           String password=DigestUtils.sha256Hex("123456");
           merchantUserService.updatePassword(password,id);
           Map<String,Object> map=new HashMap<String,Object>();
           map.put("password",123456);
           return new Result().ok(map);
       }
        return new Result().error("出错了！");
    }
    /**
     * 注册
     *
     * @return
     */
    @CrossOrigin
    @PostMapping("register")
    @ApiOperation("注册")
    public Result register(@RequestBody MerchantUserRegisterDTO dto){
        ValidatorUtils.validateEntity(dto);
        String oPassword= DigestUtils.sha256Hex(dto.getOldPassword());
        String nPassword= DigestUtils.sha256Hex(dto.getNewPassword());
        if(!oPassword.equals(nPassword)){
            return new Result().error("两次输入密码不一致，请重新输入！");
        }
        MerchantUserEntity entity=new MerchantUserEntity();
        entity.setPassword(nPassword);
        entity.setMobile(dto.getMobile());
        entity.setWeixinname(dto.getWeixinname());
        entity.setWeixinurl(dto.getWeixinurl());
        entity.setOpenid(dto.getOpenid());
        entity.setCreateDate(new Date());
        entity.setStatus(Common.STATUS_ON.getStatus());
        entity.setMerchantid("0");
        //效验数据
        ValidatorUtils.validateEntity(dto);
        //根据用户名判断是否已经注册过了
        MerchantUserEntity user = merchantUserService.getByMobile(dto.getMobile());
        if(null!=user){
            return new Result().error("改注册账号已存在，请换个账号重新注册!");
        }
        merchantUserService.insert(entity);
        return new Result();
    }
    /**
     * 修改手机号码
     * @param mobile
     * @param  id
     * @return
     */
    @CrossOrigin
    @Login
    @PutMapping("updateMobile")
    @ApiOperation("修改手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value="手机号码",required=true,paramType="query",dataType = "String"),
            @ApiImplicitParam(name="id",value="会员编号",required = true,paramType = "query",dataType = "long")
    })
    public Result  updateMobile(HttpServletRequest request,String mobile, long id){
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
    @Login
    @PutMapping("updateWeixin")
    @ApiOperation("帮定微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name="openid",value="openid",required=true,paramType="query",dataType = "String"),
            @ApiImplicitParam(name="weixinName",value="微信名称",required=true,paramType="query",dataType = "String"),
            @ApiImplicitParam(name="weixinUrl",value="微信头像",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="id",value="会员编号",required = true,paramType = "query",dataType = "long")
    })
    public Result  updateMobile(String openid,String weixinName,String weixinUrl,long id){
        merchantUserService.updateWeixin(openid,weixinName,weixinUrl,id);
        return new Result();
    }

    /**
     * 获取验证码
     * @param request
     * @param
     * @return
     */
    @CrossOrigin
    @PutMapping("code")
    @ApiOperation("获取验证码")
    public Result registerCode(HttpServletRequest request,@RequestBody String mobile){
        boolean bool= SendSMSUtil.sendCodeForRegister(mobile,request,smsConfig);
        return new Result().ok(bool);
    }
    @CrossOrigin
    @GetMapping("verifyCode")
    @ApiOperation("校验验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value="手机号",required=true,paramType="query",dataType = "String"),
            @ApiImplicitParam(name="code",value="验证码",required=true,paramType="query",dataType = "String")
    })
    public Result verifyCode(HttpServletRequest request,String mobile,String code){
        Result bool=SendSMSUtil.verifyCode(mobile,request,code);
        return bool;
    }
    /**
     *
     * @param id
     * @return
     */
    @CrossOrigin
    @Login
    @GetMapping("getMerchantAllByUserId")
    @ApiOperation("根据会员Id显示商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="会员编号",required=true,paramType="query",dataType = "Long")
    })
    public Result<List<MerchantDTO>> getMerchantAllByUserId(Long id){
        List<MerchantDTO> list=merchantUserService.getMerchantByUserId(id);
        return new Result().ok(list);
    }
    /**
     *
     * @param mobile
     * @return
     */
    @CrossOrigin
    @Login
    @GetMapping("getMerchantAllByMobile")
    @ApiOperation("根据会员手机号码示商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value="会员手机号码",required=true,paramType="query",dataType = "String")
    })
    public Result<List> getMerchantAllByMobile(String mobile){
        List list=merchantUserService.getMerchantByMobile(mobile);
        return new Result().ok(list);
    }

}