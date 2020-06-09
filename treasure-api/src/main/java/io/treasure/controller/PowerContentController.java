package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dao.PowerContentDao;
import io.treasure.dao.PowerLevelDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.PowerContentDTO;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.dto.ReceiveGiftDto;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.PowerContentEntity;
import io.treasure.entity.PowerLevelEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.PowerContentService;
import io.treasure.service.PowerLevelService;
import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
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

    @Autowired(required=false)
    private PowerLevelDao powerLevelDao;

    @Autowired(required = false)
    private PowerContentDao powerContentDao;


    @PostMapping("userPowerRegister")
    @ApiOperation("助力用户注册")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "id", value = "用户手机号", paramType = "query",required = true, dataType="long"),
            @ApiImplicitParam(name = "subjoinContent", value = "代付金数量", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "powerlevelId", value = "管理员id", paramType = "query",required = true, dataType="long"),
    })
    public Result userPowerRegister(@RequestBody ClientUserDTO dto,
                                    @RequestParam Long id,
                                    @RequestParam(name="subjoinContent",required = false) String subjoinContent,
                                    @RequestParam Long powerlevelId) {
        Long userId = id;
        Result result = new Result();
        String mobile = null;
        String amountTmp = null;
        System.out.println("got clientUserDto:"+dto.toString());
        //表单校验
        ValidatorUtils.validateEntity(dto);
        ClientUserEntity cue = clientUserService.getUserByPhone(dto.getMobile());
        if(cue != null){
            result.setCode(601);
            result.setMsg("注册失败,本活动仅支持新注册用户！");
            return result;
        }
        ClientUserEntity user = new ClientUserEntity();
        user.setMobile(dto.getMobile());
        user.setUsername(dto.getMobile());
        user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        user.setCreateDate(new Date());
        user.setUnionid(dto.getUnionid());
        user.setClientId(dto.getClientId());
        clientUserService.insert(user);

        ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(dto.getMobile());
        TokenEntity token = tokenService.createToken(userByPhone1.getId());
        TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());

        Map map = new HashMap();
        map.put("token", byUserId.getToken());
        map.put("id", userByPhone1.getId());
        map.put("id", userId);
        map.put("mobile", userByPhone1.getMobile());
        map.put("userId", id);
        System.out.println("powerlevelId:"+powerlevelId);
        PowerContentDTO powerContentByUserId = powerContentService.getPowerContentByUserId(16516459L);
        if(powerContentByUserId == null){
            result.setCode(601);
            result.setMsg("活动不存在！");
            return result;
        }

        map.put("powerPeopleSum",powerContentByUserId.getPowerPeopleSum());
        map.put("powerSum",powerContentByUserId.getPowerSum());
        map.put("powerlevelId",powerContentByUserId.getPowerlevelId());

        int img = powerLevelService.insertPowerLevel(map,powerContentByUserId);
        if (img ==3 ){
            amountTmp = String.valueOf(powerContentByUserId.getPowerSum());
            clientUserService.addRecordGiftByUserid(String.valueOf(id),amountTmp);
            ClientUserDTO clientUserDTO = clientUserService.get(id);
            if (clientUserDTO != null)
                mobile = clientUserDTO.getMobile();

            Result<Map<String,String>> result1 = new Result<>();
            //将token
            map.put("client_id",dto.getClientId());
            map.put("mobile",dto.getMobile());
            map.put("token",token.getToken());
            result1.setData(map);
            result.setCode(200);
            result.setMsg("助力成功，好友获得："+amountTmp+"代付金！");
            return result;
        }else if (img == 2){

            result.setCode(601);
            result.setMsg("活动不存在！");
            return result;
        }else{// if (img == 1){
            amountTmp = String.valueOf(powerContentByUserId.getPowerSum());
            Result<Map<String,String>> result1 = new Result<>();
            //将token
            map.put("client_id",dto.getClientId());
            map.put("mobile",dto.getMobile());
            map.put("token",token.getToken());
            result1.setCode(200);//
            result1.setData(map);
            //

            QueryWrapper<PowerLevelEntity> peqw = new QueryWrapper<>();
            peqw.eq("user_id",id);
            PowerLevelEntity powerLevelEntity = powerLevelDao.selectOne(peqw);
            String tmp = powerLevelEntity.getRamdomNumber();//得到所有随机数的值
            String tmpArray[] = tmp.split(",");

            int powerSum = powerLevelEntity.getPowerSum();
            String targetValue = null;
            if(tmpArray.length>powerSum){
                targetValue = tmpArray[powerSum];
            }

            if(targetValue != null){
                result1.setMsg("恭喜您成功为好友助力："+targetValue+"代付金！");
            }else{
                result1.setMsg("助力成功！");
            }
            return result1;
        }
        //return new Result().ok("");
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


    @GetMapping("info")
    @ApiOperation("基本信息")
    @ApiImplicitParam(name ="powerLevelId", value = "powerLevelId", paramType = "query", dataType="long")
    public Result requirePowerInfoByPowerLevelId(Long powerLevelId){
        PowerContentDTO powerContentByUserId = powerContentService.getPowerContentByUserId(16516459L);
        if(powerContentByUserId != null)
            return new Result().ok(powerContentByUserId);
        return new Result().ok("[]");
    }

}
