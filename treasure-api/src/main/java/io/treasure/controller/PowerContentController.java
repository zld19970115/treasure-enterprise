package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.PowerContentDTO;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.PowerContentService;
import io.treasure.service.PowerLevelService;
import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "long"),
            @ApiImplicitParam(name = "subjoinContent", value = "附加内容", required = true, paramType = "String")
    })
    public Result userPowerRegister(@RequestBody ClientUserDTO dto , Long userId,String subjoinContent) {
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
        map.put("user", userByPhone1);
        TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
        map.put("token", byUserId.getToken());
        PowerLevelDTO powerLevelDTO = powerLevelService.getPowerLevelByUserId(userId);
        List<PowerContentDTO> powerContentDTO = powerContentService.getPowerContentByUserId(userId);
        //判断用户是否添加过主力级别，没有得话添加
        if (powerLevelDTO == null && powerContentDTO == null){
            map.put("powerlevel",1);
            powerLevelService.insertPowerLevel(map);
        }else if (powerLevelDTO == null){
            map.put("powerlevel",2);
            powerLevelService.insertPowerLevel(map);
        }
        //根据用户助力级别，更新代付金和助力人数
        PowerLevelDTO newpowerLevelDTO = powerLevelService.getPowerLevelByUserId(userId);
        int giftType = Integer.parseInt(newpowerLevelDTO.getPowerlevel());
        String gift = "";
        if (giftType == 1 ){
            gift = "100";
        }else if (giftType == 2){
            gift = "200";
        }
        powerLevelService.updatePowerGiftByUserId(userId, Integer.parseInt(gift));
        clientUserService.addRecordGiftByUserid(String.valueOf(userId),gift);
        powerLevelService.updatePowerSumByUserId(userId);
        //添加助力信息
        if (subjoinContent != null){
            map.put("subjoin_content",subjoinContent);
            powerContentService.insertPowerContent(map);
        }

        map.put("power_type", 1 );

        return new Result().ok(map);
    }





}
