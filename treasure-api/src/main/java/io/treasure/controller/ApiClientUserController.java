package io.treasure.controller;


import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.QueryClientUserDto;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.MasterOrderService;
import io.treasure.service.RecordGiftService;
import io.treasure.service.TokenService;
import io.treasure.utils.SendSMSUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户信息")
public class ApiClientUserController {
    @Autowired
    private ClientUserService clientUserService;
    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private SMSConfig smsConfig;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RecordGiftService recordGiftService;

    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String")
    })
    public Result<PageData<ClientUserDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ClientUserDTO> page = clientUserService.page(params);

        return new Result<PageData<ClientUserDTO>>().ok(page);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<ClientUserDTO> get(@PathVariable("id") Long id) {
        ClientUserDTO data = clientUserService.get(id);

        return new Result<ClientUserDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody ClientUserDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        clientUserService.save(dto);

        return new Result();
    }

    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody ClientUserDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        clientUserService.update(dto);

        return new Result();
    }

    @Login
    @DeleteMapping
    @ApiOperation("用户删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户编码", required = true, paramType = "query")
    })
    public Result delete(Long id) {
        //效验数据
        AssertUtils.isNull(id, "id");

        ClientUserDTO clientUserDTO = clientUserService.get(id);
        clientUserDTO.setStatus(9);
        clientUserService.update(clientUserDTO);
        return new Result();
    }

    @GetMapping("isRegister")
    @ApiOperation("验证手机是否注册:true-已注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "手机号", required = true, paramType = "query")
    })
    public Result isRegister(String tel) {
        AssertUtils.isBlank(tel, "tel");
        boolean b = clientUserService.isRegister(tel);
        return new Result().ok(b);
    }

    @GetMapping("userRegisterCode")
    @ApiOperation("用户注册验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "手机号", required = true, paramType = "query")
    })
    public Result register(HttpServletRequest request, String tel) {
        boolean bool = SendSMSUtil.sendCodeForRegister(tel, request, smsConfig);
        return new Result().ok(bool);
    }

    @GetMapping("userRegisterByCode")
    @ApiOperation("用户注册验证码(返回验证码)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "手机号", required = true, paramType = "query")
    })
    public Result register(String tel) {
        Result result = SendSMSUtil.sendCodeForRegister(tel, smsConfig);
        return result;
    }

    @GetMapping("userRegisterVerifyCode")
    @ApiOperation("用户注册验证码校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "query")
    })
    public Result verifyCode(HttpServletRequest request, String tel, String code) {
        Result bool = SendSMSUtil.verifyCode(tel, request, code);
        return bool;
    }

    @PostMapping("userRegister")
    @ApiOperation("用户注册")
    public Result userRegister(@RequestBody ClientUserDTO dto) {
        //表单校验
        ValidatorUtils.validateEntity(dto);
        Map map = new HashMap();
        ClientUserEntity user = new ClientUserEntity();
        user.setMobile(dto.getMobile());
        user.setUsername(dto.getMobile());
        user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        user.setCreateDate(new Date());
        user.setClientId(dto.getClientId());
        clientUserService.insert(user);
        ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(dto.getMobile());
        tokenService.createToken(userByPhone1.getId());
        map.put("user", userByPhone1);
        TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
        map.put("token", byUserId.getToken());
        return new Result().ok(map);
    }

    @PostMapping("userLogin")
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", value = "个推ID", required = true, paramType = "query", dataType = "String")
    })
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto, String cid) {
        //表单校验
        ValidatorUtils.validateEntity(dto);

        //用户登录
        Map<String, Object> map = clientUserService.login(dto);
        clientUserService.updateCID(cid, dto.getMobile());
        return new Result().ok(map);
    }

    @Login
    @PostMapping("userLogout")
    @ApiOperation("用户退出")
    public Result logout(@ApiIgnore @RequestAttribute("userId") Long userId) {
        tokenService.expireToken(userId);
        return new Result();
    }

    @Login
    @PutMapping("editPassword")
    @ApiOperation("修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oldPassword", value = "原密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, paramType = "query")
    })
    public Result editPassword(Long id, String oldPassword, String newPassword) {
        ClientUserDTO data = clientUserService.get(id);
        data.getPassword();
        //密码错误
        if (!data.getPassword().equals(DigestUtils.sha256Hex(oldPassword))) {
            throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        } else {
            data.setPassword(DigestUtils.sha256Hex(newPassword));
        }
        clientUserService.update(data);
        return new Result();
    }

    @PutMapping("forgetPassword")
    @ApiOperation("忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "注册手机号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "重置密码", required = true, paramType = "query")
    })
    public Result forgetPassword(HttpServletRequest request, String tel, String code, String password) {
//        Result bool = SendSMSUtil.verifyCode(tel, request, code);

            ClientUserEntity clientUserEntity = clientUserService.getByMobile(tel);
            if (clientUserEntity == null) {
                throw new RenException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else {
                clientUserEntity.setPassword(DigestUtils.sha256Hex(password));

                clientUserService.updateById(clientUserEntity);
            }


        return new Result();
    }


    @GetMapping("getUserByOpenId")
    @ApiOperation("根据openId查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "微信用户唯一标识", required = true, paramType = "query", dataType = "String")})
    public Result<ClientUserEntity> getUserByOpenId(String openId) {
        ClientUserEntity userByOpenId = clientUserService.getUserByOpenId(openId);
        return new Result<ClientUserEntity>().ok(userByOpenId);
    }

    @GetMapping("estimateOpenId")
    @ApiOperation("绑定微信-根据openId查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "微信用户唯一标识", required = true, paramType = "query", dataType = "String")})
    public Result<Map> estimateOpenId(String openId) {
        ClientUserEntity userByOpenId = clientUserService.getUserByOpenId(openId);

        Map map = new HashMap();
        boolean c = false;
        if (userByOpenId == null || userByOpenId.getStatus()==9 ) {
            c = c;
            map.put("boolean", c);
        } else {
            c = true;
            TokenEntity byUserId = tokenService.getByUserId(userByOpenId.getId());
            map.put("boolean", c);
            map.put("user", userByOpenId);
            map.put("token", byUserId.getToken());
        }
        return new Result<Map>().ok(map);
    }


    @GetMapping("estimateMobile")
    @ApiOperation("绑定微信-根据mobile查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "微信用户唯一标识", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "clientId", value = "个推ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "微信或者APP", required = true, paramType = "query", dataType = "String")
    })
    public Result<Map<String, Object>> estimateOpenId(String openId, String mobile, String password, String clientId,String type) {
        ClientUserEntity userByPhone = clientUserService.getUserByPhone(mobile);
        System.out.println("typeasdasddddddddddddddddddddddsadsadas阿萨德"+type);
        if (type.equals("APP")){
            ClientUserEntity user = new ClientUserEntity();
            Map<String, Object> map = new HashMap<>();
            if (userByPhone != null) {
                if(userByPhone.getOpenid()!=null && userByPhone.getOpenid() != openId){
                    return new Result().error("该手机号已被绑定");
                }
                if(userByPhone.getStatus()==9){
                    return new Result().error("该手机号已注销");
                }
                TokenEntity byUserId = tokenService.getByUserId(userByPhone.getId());
                clientUserService.updateOpenid(openId, mobile);
                map.put("token", byUserId.getToken());
                map.put("user", userByPhone);
            } else {
                user.setMobile(mobile);
                user.setUsername(mobile);
                user.setCreateDate(new Date());
                user.setWay("2");
                user.setOpenid(openId);
                user.setPassword(DigestUtils.sha256Hex(password));
                user.setClientId(clientId);
                clientUserService.insert(user);
                ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(mobile);
                tokenService.createToken(userByPhone1.getId());
                map.put("user", userByPhone1);
                TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
                map.put("token", byUserId.getToken());
            }
            return new Result<Map<String, Object>>().ok(map);
        }else {

            ClientUserEntity user = new ClientUserEntity();
            Map<String, Object> map = new HashMap<>();
            if (userByPhone != null) {
                if(userByPhone.getUnionid()!=null && userByPhone.getUnionid() != openId){
                    return new Result().error("该手机号已被绑定");
                }
                if(userByPhone.getStatus()==9){
                    return new Result().error("该手机号已注销");
                }
                TokenEntity byUserId = tokenService.getByUserId(userByPhone.getId());
                clientUserService.updateUnionid(openId, mobile);
                map.put("token", byUserId.getToken());
                map.put("user", userByPhone);
            } else {
                user.setMobile(mobile);
                user.setUsername(mobile);
                user.setCreateDate(new Date());
                user.setWay("2");
                user.setUnionid(openId);
                user.setPassword(DigestUtils.sha256Hex(password));
                user.setClientId(clientId);
                clientUserService.insert(user);
                ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(mobile);
                tokenService.createToken(userByPhone1.getId());
                map.put("user", userByPhone1);
                TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
                map.put("token", byUserId.getToken());
            }
            return new Result<Map<String, Object>>().ok(map);
        }

    }

    @PutMapping("userGiftToUser")
    @ApiOperation("用户给用户转移赠送金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name     = "mobile", value = "用户要充值得用户的电话", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "giftMoney", value = "用户要充值得用户的赠送金金额", required = true, paramType = "query", dataType = "BigDecimal")
    })
    public Result userGiftToUser(@RequestParam long userId, @RequestParam String mobile, @RequestParam BigDecimal giftMoney) {

        Result result = clientUserService.userGiftToUser(userId, mobile, giftMoney);
        return new Result().ok(result);
    }

    @GetMapping("getMobileByUserId")
    @ApiOperation("根据用户id查询手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "long")
    })
    public Result getMobileByUserId(@RequestParam long userId) {
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity == null) {
            return new Result().error("此用户不存在");
        }
        return new Result().ok(clientUserEntity.getMobile());
    }
    @GetMapping("userCancel")
    @ApiOperation("用户注销")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "long")
    })
    public Result userCancel(@RequestParam long userId) {
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity == null) {
            return new Result().error("此用户不存在");
        }
        List<MasterOrderEntity> list = masterOrderService.selectByUserId(userId);
        if (list.size()!=0){
            return new Result().error("您有订单未完成");
        }

        clientUserEntity.setStatus(9);
        clientUserService.updateById(clientUserEntity);
        return new Result().ok("用户已注销");
    }

    @Login
    @GetMapping("/list")
    @ResponseBody
    String selectListByCondition(@RequestParam(name = "integral",defaultValue = "0",required = false)int integral,
                                 @RequestParam(name = "integralConditionType",defaultValue = "0",required = false)int integralConditionType,
                                 @RequestParam(name = "coin",defaultValue = "0",required = false)int coin,
                                 @RequestParam(name = "coinConditionType",defaultValue = "0",required = false)int coinConditionType,
                                 @RequestParam(name = "gift",defaultValue = "0",required = false)int gift,
                                 @RequestParam(name = "giftConditionType",defaultValue = "0",required = false)int giftConditionType,
                                 @RequestParam(name = "level",defaultValue = "0",required = false)int level,
                                 @RequestParam(name = "levelConditionType",defaultValue = "0",required = false)int levelConditionType,
                                 @RequestParam(name = "createDateConditionType",defaultValue = "0",required = false)int createDateConditionType,
                                 @RequestParam(name = "startTime",defaultValue = "null",required = false)Date startTime,
                                 @RequestParam(name = "stopTime",defaultValue = "null",required = false)Date stopTime
                                ){
        List<ClientUserEntity> clientUserEntities = clientUserService.selectListByCondition(generalQueryClientUserDto(integral,integralConditionType,coin,coinConditionType,
        gift,giftConditionType,level,levelConditionType,createDateConditionType,startTime,stopTime));
        if(clientUserEntities.size() == 0)
            return "没有内容";
        Gson gson = new Gson();
        return gson.toJson(clientUserEntities);
    }

    public QueryClientUserDto generalQueryClientUserDto(int integral,int integralConditionType,int coin,int coinConditionType,int gift,int giftConditionType,
                                                        int level,int levelConditionType,int createDateConditionType, Date startTime,Date stopTime){
        QueryClientUserDto queryClientUserDto = new QueryClientUserDto();
        queryClientUserDto.setIntegral(new BigDecimal(integral+""));
        queryClientUserDto.setCoin(new BigDecimal(coin+""));
        queryClientUserDto.setGift(new BigDecimal(gift+""));
        queryClientUserDto.setLevel(level);
        queryClientUserDto.setIntegralConditionType(integralConditionType)
                .setCoinConditionType(coinConditionType)
                .setGiftConditionType(giftConditionType)
                .setLevelConditionType(levelConditionType)
                .setCreateDateConditionType(createDateConditionType);
        if(startTime != null)
            queryClientUserDto.setStartTime(startTime);
        if(stopTime != null)
            queryClientUserDto.setStopTime(stopTime);
        return queryClientUserDto;
    }
    @GetMapping("getRecondGiftcharge")
    @ApiOperation("获取代付金转增记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "long")
    })
    public Result getRecondGiftcharge(@RequestParam long userId) {
        List<RecordGiftDTO> recordGiftDTOS = recordGiftService.selectByUserId(userId);
        return new Result().ok(recordGiftDTOS);
    }
    @GetMapping("registerGetGift")
    @ApiOperation("用户注册领取代付金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "long")
    })
    public Result registerGetGift(@RequestParam long userId) {
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity == null) {
            return new Result().error("此用户不存在");
        }
        BigDecimal a = new BigDecimal("200");
        BigDecimal gift = clientUserEntity.getGift();

        BigDecimal newGift = a.add(gift);
        clientUserEntity.setGift(newGift);
        clientUserService.updateById(clientUserEntity);
        return new Result().ok("领取成功");
    }
}