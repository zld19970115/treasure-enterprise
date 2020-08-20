package io.treasure.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import io.treasure.dao.BusinessManagerDao;
import io.treasure.dao.ClientUserDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.entity.*;
import io.treasure.service.*;
import io.treasure.utils.SendSMSUtil;
import io.treasure.vo.LevelVo;
import io.treasure.vo.PageTotalRowData;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.text.ParseException;
import java.util.*;
import java.util.Base64.Decoder;


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
    @Autowired(required = false)
    private ClientUserDao clientUserDao;
    @Autowired
    private UserWithdrawService userWithdrawService;
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
    @GetMapping("isToken")
    @ApiOperation("验证token是否可用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query")
    })
    public Result isToken(String token) {
        TokenEntity byToken = tokenService.getByToken(token);
        if (byToken==null){
            return new Result().ok(false);
        }else {
            if (byToken.getExpireDate().before(new Date())) {
                return new Result().ok(true);
            }else {
                return new Result().ok(false);
            }
        }

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
    @ApiOperation("用户注册(已删除)")
    public Result userRegister(@RequestBody ClientUserDTO dto) {
        //表单校验
        ValidatorUtils.validateEntity(dto);
        Map map = new HashMap();
        ClientUserEntity user = new ClientUserEntity();
        user.setLevel(1);
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
        return new Result().ok(map);
    }

    @PostMapping("userLogin")
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "unionid", value = "unionid", required = true, paramType = "query", dataType = "String")
    })
    public Result login(String mobile,String unionid) {
        //用户登录
        Result  result = clientUserService.login(mobile,unionid);
     //   clientUserService.updateCID(cid, mobile);
        return new Result().ok(result);
    }

    @Login
    @PostMapping("userLogout")
    @ApiOperation("用户退出")
    public Result logout(@ApiIgnore @RequestAttribute("userId") Long userId) {
        tokenService.expireToken(userId);
        return new Result();
    }
    /**
     * 解密并且获取用户手机号码
     * @param encrypdata
     * @param ivdata
     * @param sessionkey
     * @return
     * @throws Exception
     */
    @GetMapping("getMobile")
    @ApiOperation("解密并且获取用户手机号码")
    public @ResponseBody String deciphering(String encrypdata,
                                            String ivdata, String sessionkey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                                            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        byte[] encrypData = Base64Utils.decodeFromString(encrypdata);
        byte[] ivData = Base64Utils.decodeFromString(ivdata);
        byte[] sessionKey = Base64Utils.decodeFromString(sessionkey);
        String resultString = null;
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivData);
        SecretKeySpec keySpec = new SecretKeySpec(sessionKey, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            resultString = new String(cipher.doFinal(encrypData), "UTF-8");
        } catch (Exception e) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            resultString = new String(cipher.doFinal(encrypData), "UTF-8");
        }
        JSONObject object = JSONObject.parseObject(resultString);
        // 拿到手机号码
        String phone = object.getString("phoneNumber");
        return phone;
    }

//
//    public static String getWxMiniPhone(String sessionkey, String iv, String encryptedData)
//            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
//            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
//
//        byte[] encrypData = Base64Utils.decodeFromString(encryptedData);
//        byte[] ivData = Base64Utils.decodeFromString(iv);
//        byte[] sessionKey = Base64Utils.decodeFromString(sessionkey);
//        String resultString = null;
//        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivData);
//        SecretKeySpec keySpec = new SecretKeySpec(sessionKey, "AES");
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//            resultString = new String(cipher.doFinal(encrypData), "UTF-8");
//        } catch (Exception e) {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//            resultString = new String(cipher.doFinal(encrypData), "UTF-8");
//        }
//        JSONObject object = JSONObject.parseObject(resultString);
//        // 拿到手机号码
//        String phone = object.getString("phoneNumber");
//        return phone;
//    }
//
//
//    public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
//        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//        //解析解密后的字符串
//        return new String(cipher.doFinal(encData),"UTF-8");
//    }
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
            tokenService.createToken(userByOpenId.getId());
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
                tokenService.createToken(userByPhone.getId());
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
                tokenService.createToken(userByPhone.getId());
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
    @Login
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

    @Login
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
    @Login
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
        List list1 = new ArrayList();
        list1.add(userId);
        tokenService.deleteBatchIds(list1);
        clientUserEntity.setUsername(clientUserEntity.getUsername()+"已注销");
        clientUserEntity.setClientId("0");
        clientUserEntity.setMobile(clientUserEntity.getMobile()+"已注销");
        clientUserEntity.setOpenid("0");
        clientUserEntity.setUnionid("0");
        clientUserEntity.setStatus(9);
        clientUserService.updateById(clientUserEntity);
        return new Result().ok("用户已注销");
    }
    /**
     * 根据 条件查询所有提现信息列表
     * @return
     */
    @CrossOrigin
    @Login
    @GetMapping("/list")
    @ApiOperation(value = "用户赠送金列表",tags = "用户赠送金列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime",value="开始时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name ="stopTime",value = "结束时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false),
            @ApiImplicitParam(name="gift",value = "赠送金不小于",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="coin",value = "金币不小于",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="integral",value = "积分不小于",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="balance",value = "级别不小于",dataType = "int",paramType = "query",required = false)
    })
    public Result requireItems(Date startTime,Date stopTime,
                               Integer index,Integer itemNum,Integer gift,
                               Integer coin,Integer integral,Integer balance) throws ParseException {
        QueryWrapper<ClientUserEntity> mweqw = new QueryWrapper<>();

        if(startTime != null && stopTime != null){
            mweqw.between("create_date",startTime,stopTime);
        }else if(startTime != null){
            mweqw.gt("create_date",startTime);//大于
        }else if(stopTime != null){

            mweqw.lt("create_date",stopTime);
        }
        if(gift != null)
            mweqw.gt("gift",gift);
        if(coin != null)
            mweqw.gt("coin",coin);
        if(integral != null)
            mweqw.gt("integral",integral);
        if(balance != null)
            mweqw.gt("balance",balance);

        Page<ClientUserEntity> map = new Page<ClientUserEntity>(index,itemNum);
        IPage<ClientUserEntity> cue = clientUserDao.selectPage(map, mweqw);

        return new Result().ok(cue);
        //return new Result().ok(merchantWithdrawEntityIPage.getRecords());
    }
    @GetMapping("getRecondGiftcharge")
    @ApiOperation("获取代付金转增记录")
    @ApiImplicitParams({

            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "long")
    })
    public Result<PageData<RecordGiftDTO>> getRecondGiftcharge(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<RecordGiftDTO> page  = recordGiftService.selectByUserId(params);
        return new Result().ok(page);
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
       List<ClientUserEntity> list = clientUserService.selectZhuXiao(clientUserEntity.getMobile()+"已注销");
        if (list.size()!=0){
            return new Result().error("此用户已经领取过");
        }
        BigDecimal a = new BigDecimal("200");
        BigDecimal gift = clientUserEntity.getGift();
        BigDecimal newGift = a.add(gift);
        clientUserEntity.setGift(newGift);
        clientUserService.updateById(clientUserEntity);
        return new Result().ok("领取成功");
    }

    @GetMapping("pagePC")
    @ApiOperation("分页查询PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate",value="开始时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name ="endDate",value = "结束时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "String")
    })
    public Result<PageTotalRowData<ClientUserDTO>> pagePC(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageTotalRowData<ClientUserDTO>>().ok(clientUserService.pagePC(params));
    }


    @ApiOperation("手机号")
    @ApiImplicitParam(name = "mobile",value="手机号",dataType = "string",paramType = "query",required = false)
    @GetMapping("logOffCount")
    public Result getLogOffCount(String mobile){
        int res = 0;
        Integer integer = clientUserDao.selectLogOffCount(mobile+"已注销");
        if (integer != null)
            res = integer;
        return new Result().ok("数量："+res);
    }


    @Autowired(required = false)
    BusinessManagerDao businessManagerDao;

    @ApiOperation("业务员")
    @ApiImplicitParam(name = "mobile",value="手机号",dataType = "string",paramType = "query",required = false)
    @GetMapping("is_service")
    public Result isService(String mobile){
        QueryWrapper<BusinessManagerEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        queryWrapper.eq("is_service",1);
        queryWrapper.or().eq("emergent_contact",mobile);
        Integer integer = businessManagerDao.selectCount(queryWrapper);
        int res = 0;
        if(integer != null)
            res = integer;
        return new Result().ok(res);
    }
    @CrossOrigin
    @Login
    @PutMapping("inserZFB")
    @ApiOperation("绑定商户支付宝")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "ali_account_number", value = "收款支付宝账户", paramType = "query", required = false, dataType = "string"),
            @ApiImplicitParam(name = "ali_account_realname", value = "支付宝收款人真实姓名", paramType = "query", required = false, dataType = "string")
    })
    public Result inserZFB(@ApiIgnore @RequestParam Map<String, Object> params){
        String userId = (String) params.get("userId");
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("没有该用户");
        }
        if (clientUserEntity.getAliAccountNumber()!=null||clientUserEntity.getAliAccountRealname()!=null){
            return new Result().ok("1");//已绑定支付宝
        }
        String ali_account_number = (String) params.get("ali_account_number");
        String ali_account_realname = (String) params.get("ali_account_realname");
        if (ali_account_number!=null&&ali_account_realname!=null){
            clientUserEntity.setAliAccountNumber(ali_account_number);
            clientUserEntity.setAliAccountRealname(ali_account_realname);
            clientUserService.updateById(clientUserEntity);
            return new Result().ok("绑定成功");
        }


        return new Result().ok("0");//未绑定支付宝
    }
    @CrossOrigin
    @Login
    @PutMapping("inserWX")
    @ApiOperation("绑定用户微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "wx_account_openid", value = "收款微信openid", paramType = "query", required = false, dataType = "string"),
            @ApiImplicitParam(name = "status", value = "APP/VX", paramType = "query", required = true, dataType = "string")
    })
    public Result inserWX(@ApiIgnore @RequestParam Map<String, Object> params){
        String userId = (String) params.get("userId");
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().ok("没有该用户");
        }
        if (StringUtils.isNotBlank(clientUserEntity.getWxAccountOpenid())){
            return new Result().ok("1");//已绑定微信
        }
        String wx_account_openid = (String) params.get("wx_account_openid");
        String status = (String) params.get("status");
        if (wx_account_openid!=null){
            if ("VX".equals(status) || status == "VX"){
                clientUserEntity.setWxStatus(1);
            }else {
                clientUserEntity.setWxStatus(2);
            }
            clientUserEntity.setWxAccountOpenid(wx_account_openid);
            clientUserService.updateById(clientUserEntity);
            return new Result().ok(clientUserEntity.getMobile());
        }
        return new Result().ok("0");//未绑定微信
    }
    @CrossOrigin
    @Login
    @PutMapping("deleltWX")
    @ApiOperation("解除绑定用户微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true, dataType = "long")
    })
    public Result deleltWX(@ApiIgnore @RequestParam Map<String, Object> params){
        String userId = (String) params.get("userId");
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("没有该用户");
        }
        clientUserService.updateWX(userId);
        return new Result().ok("解绑成功");//未绑定微信
    }
    @Login
    @GetMapping("deleltWXByCode")
    @ApiOperation("用户解绑验证码(返回验证码)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tel", value = "手机号", required = true, paramType = "query")
    })
    public Result deleltWXByCode(String tel) {
        Result result = SendSMSUtil.sendCodeFordeletWx(tel, smsConfig);
        return result;
    }

    @CrossOrigin
    @Login
    @PutMapping("selectWx")
    @ApiOperation("查询用户是否绑定微信提现openid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true, dataType = "long")
    })
    public Result selectWx(@ApiIgnore @RequestParam Map<String, Object> params){
        String userId = (String) params.get("userId");
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        Map map = new HashMap();
        if (clientUserEntity==null){
            return new Result().error("没有该用户");
        }
        if (clientUserEntity.getWxAccountOpenid()!=null){
            map.put("status",1);
            map.put("userId",clientUserEntity.getMobile());
            return new Result().ok(map);//绑定微信
        }else {
            map.put("status",2);
            return new Result().ok(map);//未绑定微信
        }
    }
    @Login
    @GetMapping("getCoin")
    @ApiOperation("获取可提现现金余额和已提现余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true, dataType="Long")
    })
    public Result getCoin(@RequestParam Long userId){
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        Map map = new HashMap();
        if (clientUserEntity==null){
            return new Result().error("没有该用户");
        }

        map.put("can",clientUserEntity.getCoin());
        List<UserWithdrawDTO> userWithdrawDTOS = userWithdrawService.selectByUserIdAndalready(userId);
        Double d = 0.00;
        for (UserWithdrawDTO userWithdrawDTO : userWithdrawDTOS) {
            d = d + userWithdrawDTO.getMoney();
        }
        map.put("already",d);
        return new Result().ok(map);
    }

    @Login
    @GetMapping("/CoinToBalance")
    @ApiOperation("现金兑换宝币")
    public Result CoinToBalance(@RequestParam Long userId, @RequestParam BigDecimal coin){

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return  new Result().error("没有找到该用户，请稍后再试");
        }
        BigDecimal coin1 = clientUserEntity.getCoin();
        BigDecimal balance = clientUserEntity.getBalance();
        if (coin1.compareTo(coin)==-1){
            return  new Result().error("现金不足");
        }
        String ctob = clientUserService.selectCoinToBalance();
        double v = Double.parseDouble(ctob);
        double v1 = coin.doubleValue();
        if (v1<v){
            return  new Result().error("现金不足"+v+"元");
        }
        BigDecimal subtract = coin1.subtract(coin);
        BigDecimal add = balance.add(coin);
        clientUserEntity.setCoin(subtract);
        clientUserEntity.setBalance(add);
        clientUserService.updateById(clientUserEntity);
        return  new Result().ok("兑换成功");
    }



    @GetMapping("/getBlanceforLevelList")
    @ApiOperation("根据会员等级获取可领取列表")
    public Result getBlanceforLevelList(@RequestParam Long userId){
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return  new Result().error("没有找到该用户，请稍后再试");
        }
        Integer level = clientUserEntity.getLevel();
         if (level==null){
             return  new Result().error("您不是会员，请消费后重试");
         }
//        if (level==1){
//            return  new Result().error("您的会员级别不足，请升级后再试");
//        }
       List list = new ArrayList();
        List<LevelStatusEntity> levelStatusEntities = clientUserService.selectLevelStatus(userId);
        for (Integer i = 2; i <= 5; i++) {
            LevelVo vo = new LevelVo();
            if (i<=level){
                for (LevelStatusEntity levelStatusEntity : levelStatusEntities) {
                    if (levelStatusEntity.getLeveled() == i){
                        vo.setStatus(1);
                    }
                }
            }else {
                vo.setStatus(2);
            }
            vo.setLevel(i);
            BigDecimal bigDecimal = clientUserService.selectBlanceForLevel(i);

            vo.setBlance(bigDecimal);
            String picture = clientUserService.selectpictureForLevel(i);
            vo.setPicture(picture);

            list.add(vo);
        }
        return  new Result().ok(list);
    }

    @GetMapping("/getBlanceforLevel")
    @ApiOperation("根据会员等级获取宝币")
    public Result getBlanceforLevel(@RequestParam Long userId,@RequestParam Integer level, @RequestParam BigDecimal balance) {

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity == null) {
            return new Result().error("没有找到该用户，请稍后再试");
        }
        clientUserService.insertLevelStatus(userId, level, balance);
        BigDecimal clientUserEntityBalance = clientUserEntity.getBalance();
        BigDecimal add = balance.add(clientUserEntityBalance);
        clientUserEntity.setBalance(add);
        clientUserService.updateById(clientUserEntity);
        return new Result().ok("成功");
    }
}