package io.treasure.controller;
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

import io.treasure.entity.MerchantUserEntity;
import io.treasure.service.MerchantUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value ="1", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "10", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantUserDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantUserDTO> page = merchantUserService.page(params);

        return new Result<PageData<MerchantUserDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MerchantUserDTO> get(@PathVariable("id") Long id){
        MerchantUserDTO data = merchantUserService.get(id);

        return new Result<MerchantUserDTO>().ok(data);
    }

    /**
     * 会员注册
     * @param dto
     * @return
     */
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantUserDTO dto){
            //效验数据
            ValidatorUtils.validateEntity(dto);
            String mobile=dto.getMobile();//注册账号、手机号码
            //根据用户名判断是否已经注册过了
            MerchantUserEntity user = merchantUserService.getByMobile(mobile);
            if(null!=user){
                return new Result().error("改注册账号已存在，请换个账号重新注册!");
             }
            dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
            dto.setCreateDate(new Date());
            merchantUserService.save(dto);
            return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantUserDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        dto.setUpdateDate(new Date());
        merchantUserService.update(dto);
        return new Result();
    }
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        merchantUserService.delete(ids);
        return new Result();
    }
    @PostMapping("login")
    @ApiOperation("登录")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);
        //用户登录
        Map<String, Object> map = merchantUserService.login(dto);
        return new Result().ok(map);
    }

//    @Login
//    @PostMapping("logout")
//    @ApiOperation("退出")
//    public Result logout(@ApiIgnore @RequestAttribute("userId") Long userId){
//        //tokenService.expireToken(userId);
//        return new Result();
//    }
}