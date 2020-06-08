package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.PowerContentDTO;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.dto.ReceiveGiftDto;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.PowerContentService;
import io.treasure.service.PowerLevelService;
import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力信息
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:26
 */
@RestController
@RequestMapping("/powercontent")
@Api(tags = "助力信息")
public class PowerContentController {

    @Autowired
    private ClientUserService clientUserService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PowerLevelService powerLevelService;
    @Autowired
    private PowerContentService powerContentService;


    @PostMapping("userPowerRegister")
    @ApiOperation("助力用户注册")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "id", value = "用户手机号", paramType = "query",required = true, dataType="String"),
            @ApiImplicitParam(name = "subjoinContent", value = "代付金数量", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "powerlevelId", value = "管理员id", paramType = "query",required = true, dataType="String"),
    })
    public Result userPowerRegister(@RequestBody ClientUserDTO dto, Long id,String subjoinContent,Long powerlevelId) {
        Long userId = id;
        //表单校验
        ValidatorUtils.validateEntity(dto);
        Map map = new HashMap();
        ClientUserEntity user = new ClientUserEntity();
        user.setMobile(dto.getMobile());
        user.setUsername(dto.getMobile());
        user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        user.setCreateDate(new Date());
        user.setUnionid(dto.getUnionid());
        user.setClientId(dto.getClientId());
        clientUserService.insert(user);
        ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(dto.getMobile());
        tokenService.createToken(userByPhone1.getId());
        TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
        map.put("token", byUserId.getToken());
        map.put("id", userByPhone1.getId());
        map.put("id", userId);
        map.put("mobile", userByPhone1.getMobile());
        map.put("userId", id);
        PowerContentDTO powerContentDTO =  powerContentService.getPowerContentByUserId(powerlevelId);

        map.put("powerPeopleSum",powerContentDTO.getPowerPeopleSum());
        map.put("powerSum",powerContentDTO.getPowerSum());
        map.put("powerlevelId",powerContentDTO.getPowerlevelId());
        int img = powerLevelService.insertPowerLevel(map,powerContentDTO);
        if (img ==3 ){
            clientUserService.addRecordGiftByUserid(String.valueOf(id), String.valueOf(powerContentDTO.getPowerSum()));
            map.put("msg","助力完成");
        }else if (img == 2){
            map.put("msg","注册失败");
        }else if (img == 1){
            map.put("msg","注册成功");
        }


        return new Result().ok(map);
    }

    @PostMapping("insertPowerContent")
    @ApiOperation("添加助力信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "powerName", value = "助力活动名称", required = true, paramType = "String"),
            @ApiImplicitParam(name = "powerType", value = "助力奖励类别（1-代付金，2-商品，3-菜品）", required = true, paramType = "int"),
            @ApiImplicitParam(name = "powerSum", value = "助力奖励数量", required = true, paramType = "int"),
            @ApiImplicitParam(name = "powerPeopleSum", value = "助力人数", required = true, paramType = "int"),
            @ApiImplicitParam(name = "subjoinContent", value = "活动内容", required = true, paramType = "String"),
            @ApiImplicitParam(name = "goodId", value = "菜品id", required = true, paramType = "Long"),
            @ApiImplicitParam(name = "merchandiseId", value = "商品id", required = true, paramType = "Long"),
            @ApiImplicitParam(name = "activityOpenDate", value = "活动开始时间", required = true, paramType = "String"),
            @ApiImplicitParam(name = "activityAbortDate", value = "活动截止日期", required = true, paramType = "String"),
    })
    public Result insertPowerContent(@RequestBody Map<String,Object> params) {
        //表单校验
    int img =  powerContentService.insertPowerContent(params);
       if (img == 1){
           return new Result().ok("添加成功");
       }else {
           return new Result().ok("添加失败");
       }


    }

    //, Long id,String subjoinContent,Long powerlevelId
    @PostMapping("upr")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "id", value = "用户手机号", paramType = "query",required = true, dataType="long"),
            @ApiImplicitParam(name = "subjoinContent", value = "代付金数量", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "powerlevelId", value = "管理员id", paramType = "query",required = true, dataType="long"),
    })
    @ApiOperation("助力用户注册")
    public Result<String> helperRegist(@RequestBody ClientUserDTO dto, Long id,String subjoinContent,Long powerlevelId) {

        Long userId = id;
        //表单校验
        ValidatorUtils.validateEntity(dto);
        Map map = new HashMap();
        ClientUserEntity user = new ClientUserEntity();
        user.setMobile(dto.getMobile());
        user.setUsername(dto.getMobile());
        user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        user.setCreateDate(new Date());
        user.setUnionid(dto.getUnionid());
        user.setClientId(dto.getClientId());
        clientUserService.insert(user);
        ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(dto.getMobile());
        tokenService.createToken(userByPhone1.getId());
        TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
        map.put("token", byUserId.getToken());
        map.put("id", userByPhone1.getId());
        map.put("id", userId);
        map.put("mobile", userByPhone1.getMobile());
        map.put("userId", id);
        PowerContentDTO powerContentDTO =  powerContentService.getPowerContentByUserId(powerlevelId);

        map.put("powerPeopleSum",powerContentDTO.getPowerPeopleSum());
        map.put("powerSum",powerContentDTO.getPowerSum());
        map.put("powerlevelId",powerContentDTO.getPowerlevelId());
        int img = powerLevelService.insertPowerLevel(map,powerContentDTO);
        if (img ==3 ){
            clientUserService.addRecordGiftByUserid(String.valueOf(id), String.valueOf(powerContentDTO.getPowerSum()));
            map.put("msg","助力完成");
        }else if (img == 2){
            map.put("msg","注册失败");
        }else if (img == 1){
            map.put("msg","注册成功");
        }


        return new Result().ok(map);

    }




}
