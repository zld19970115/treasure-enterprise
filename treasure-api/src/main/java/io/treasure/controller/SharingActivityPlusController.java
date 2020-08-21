package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.RecordGiftDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dto.SharingActivityDTO;
import io.treasure.enm.ESharingActivity;
import io.treasure.enm.ESharingInitiator;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.*;
import io.treasure.service.*;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.HelpSharingActivityVo;
import io.treasure.vo.ProposeSharingActivityVo;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sharing_activity")
@Api(tags="助力活动综合内容")
public class SharingActivityPlusController {

    @Autowired
    private ClientUserService clientUserService;
    @Autowired
    private TokenService tokenService;

    @Autowired(required = false)
    private ClientUserDao clientUserDao;
    @Autowired
    private SharingActivityService sharingActivityService;
    @Autowired
    private SharingActivityExtendsService sharingActivityExtendsService;
    @Autowired
    private SharingInitiatorService sharingInitiatorService;
    @Autowired
    private SharingActivityLogService sharingActivityLogService;
    @Autowired(required = false)
    private SharingActivityLogDao sharingActivityLogDao;
    @Autowired
    private SharingAndDistributionParamsService sharingAndDistributionParamsService;
    @Autowired
    private DistributionRewardServiceImpl distributionRewardService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private SharingRewardGoodsRecordService sharingRewardGoodsRecordService;
    @Autowired(required = false)
    private RecordGiftDao recordGiftDao;
    private BigDecimal balanceLimit = new BigDecimal("200");

    @PostMapping("startRelay")
    @ApiOperation("发起助力")
    public Result startRelay(@RequestBody ProposeSharingActivityVo vo) throws Exception{
        Result result = new Result();
        Map<String,Object> map = new HashMap<>();

        Long id = vo.getId();
        Integer saId = vo.getSaId();
        System.out.println("get requestBody info(id,said):"+id+","+saId);

        //0、检查活动是否存在(只输入活动id)
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, false);
        if(saItem == null){
            return new Result().error("活动("+saId+")不存在！");
        }else{
            if(saItem.getCloseDate().getTime()<= new Date().getTime()){
                return new Result().error(saItem.getSubject()+"活动已结束！");
            }else
            if(saItem.getOpenDate().getTime()> new Date().getTime()){
                return new Result().error(saItem.getSubject()+"活动尚未开始！");
            }
        }

        map.put("helpersNum",saItem.getHelpersNum());       //需要助力人数
        map.put("rewardAmount",saItem.getRewardAmount());   //姿励的数量
        map.put("rewardType",saItem.getRewardType());       //奖励的类型
        map.put("body",saItem.getHelpersNum());

        //1、查看助力列表
        List<SharingActivityHelpedEntity> helpedListComboUnread = sharingActivityLogService.getHelpedListComboUnread(id,saId,saItem.getHelpersNum());;
        map.put("helpers",helpedListComboUnread);

        //2、助力者相关基本信息,名称和头像
        ClientUserEntity clientUser = clientUserService.getClientUser(id);
        String headImage = null;
        String clientName = null;
        if(clientUser != null){
            headImage = clientUser.getHeadImg();
            clientName = clientUser.getUsername();
            if(clientName== null)
                clientName = clientUser.getMobile();
        }
        map.put("client_name",clientName);
        map.put("initiator_head_img",headImage);

        //3、初始化数据(新助力则重新插入，非新助力则会返回原助力信息)
        SharingInitiatorEntity siEntity = new SharingInitiatorEntity();
        siEntity.setSaId(saId);
        siEntity.setInitiatorId(id);
        siEntity.setRewardType(saItem.getRewardType());
        siEntity.setRewardValue(saItem.getRewardAmount());
        siEntity.setStatus(ESharingInitiator.IN_PROCESSING.getCode());
        siEntity.setStartTime(saItem.getOpenDate());
        siEntity.setFinishedTime(saItem.getCloseDate());
        SharingInitiatorEntity sharingInitiatorEntity = sharingInitiatorService.insertOne(siEntity);

        String finishStamp = TimeUtil.dateToStamp(saItem.getCloseDate());
        map.put("sharing",sharingInitiatorEntity);
        map.put("finishStamp",finishStamp);

        result.setData(map);

        if(sharingInitiatorEntity!= null){
            result.setMsg("成功发起："+saItem.getSubject()+"活动");
            result.setCode(200);
            return result;
        }

        result.setMsg("活动发起失败，请稍后重试！");
        result.setData(map);
        result.setCode(500);
        return result;
    }

    @GetMapping("sReaded")
    @ApiOperation("更新助力活动为已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "client_id", value = "客户id", paramType = "query", required = true, dataType="long") ,
            @ApiImplicitParam(name = "sharingId", value = "活动id", paramType = "query",required = true, dataType="int")
    })
    public Result readed(Long client_id,Integer sharingId){
        sharingInitiatorService.setReadedStatus(client_id,sharingId);

        return new Result().ok("已更新");
    }

    public List<SharingActivityHelpedEntity> getHelperList(SharingInitiatorEntity siItem,Integer sharingMethod,Integer helpersNum,boolean isHelper){
        List<SharingActivityHelpedEntity> helpedListCombo = null;

        //如果当前活动为无限模式，则显示助力列表
        if(sharingMethod == ESharingActivity.SharingMethod.INFINITE_METHOD.getCode()){//无限模式
            //若为自本人，则只显示未读的内容，
            if(isHelper){
                helpedListCombo = sharingActivityLogService.getHelpedListComboByProposeId(siItem.getInitiatorId(), siItem.getSaId(),siItem.getProposeId(),helpersNum);
            }else{
                if(siItem.getReaded() == 1)
                    helpedListCombo = sharingActivityLogService.getHelpedListComboByProposeId(siItem.getInitiatorId(), siItem.getSaId(),siItem.getProposeId(),helpersNum);
            }
        }else{
            if(siItem.getReaded() == 1){//仅显示未读的记录内容
                helpedListCombo = sharingActivityLogService.getHelpedListComboByProposeId(siItem.getInitiatorId(), siItem.getSaId(),siItem.getProposeId(),helpersNum);
            }
        }
        return helpedListCombo;
    }
    public Result initSharingActivityInfo(SharingActivityEntity saItem,Long initiatorId,String msg,boolean isError,boolean isHelper)throws Exception{
        Result result = new Result();
        Map<String,Object> map = new HashMap<>();
        result.setMsg(msg);//设置返回信息
        if(isError){
            result.setCode(501);
        }else{
            result.setCode(200);
        }

        if(saItem == null){
            map.put("initiator_head_img",null);
            map.put("client_name",null);
            map.put("finishStamp",null);
            map.put("helpersNum",null);
            map.put("rewardAmount",null);
            map.put("body",null);
            map.put("helpers",null);

            result.setData(map);
            return result;
        }

        Integer saId =  saItem.getSaId();
        Integer sharingMethod = saItem.getSharingMethod();
        Integer helpersNum = saItem.getHelpersNum();
        Integer rewardAmount = saItem.getRewardAmount();
        String finishStamp = TimeUtil.dateToStamp(saItem.getCloseDate());

        ClientUserEntity clientUser = clientUserService.getClientUser(initiatorId);
        if(clientUser == null){
            map.put("initiator_head_img",null);
            map.put("client_name",null);
            map.put("finishStamp",finishStamp);
            map.put("helpersNum",helpersNum);
            map.put("rewardAmount",rewardAmount);
            map.put("body",helpersNum);
            map.put("helpers",null);

            result.setData(map);
            return result;
        }

        String headImage = clientUser.getHeadImg();
        String client_name = clientUser.getUsername();
        if(client_name== null)
            client_name = clientUser.getMobile();

        SharingInitiatorEntity currentOne = sharingInitiatorService.getCurrentOne(initiatorId, saId);
        if(currentOne == null){
            map.put("initiator_head_img",headImage);
            map.put("client_name",client_name);
            map.put("finishStamp",finishStamp);
            map.put("helpersNum",helpersNum);
            map.put("rewardAmount",rewardAmount);
            map.put("body",helpersNum);
            map.put("helpers",null);

            result.setData(map);
            return result;
        }

        List<SharingActivityHelpedEntity> helpedListCombo = getHelperList(currentOne,sharingMethod,saItem.getHelpersNum(),isHelper);

        map.put("initiator_head_img",headImage);
        map.put("client_name",client_name);
        map.put("finishStamp",finishStamp);
        map.put("helpersNum",helpersNum);
        map.put("rewardAmount",rewardAmount);
        map.put("body",helpersNum);
        map.put("helpers",helpedListCombo);

        result.setData(map);
        return result;
    }


    @PostMapping("sharingInfo")
    @ApiOperation("助力信息")
    public Result sharingInfo(@RequestBody ProposeSharingActivityVo vo) throws Exception{

        Result result = new Result();
        Map<String,Object> map = new HashMap<>();

        Long id = vo.getId();
        Integer saId = vo.getSaId();
        System.out.println("get requestBody info(id,said):"+id+","+saId);

        //1、活动不存在或无效则返回
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, false);
        int helpersNum = 0;
        if(saItem == null){
            return initSharingActivityInfo(null,id,"活动("+saId+")不存在！",true,false);
        }else{
            helpersNum = saItem.getHelpersNum();
            if(saItem.getCloseDate().getTime()<= new Date().getTime()){
                return initSharingActivityInfo(saItem,id,saItem.getSubject()+"活动已结束！",true,false);
            }else
            if(saItem.getOpenDate().getTime()> new Date().getTime()){
                return initSharingActivityInfo(saItem,id,saItem.getSubject()+"活动尚未开始！",true,false);
            }

        }
        //2、初始化数据
        val currentOne = sharingInitiatorService.getCurrentOne(vo.getId(), vo.getSaId());
        switch(currentOne.getStatus()){
            case 1:
                return initSharingActivityInfo(saItem,id,saItem.getSubject()+"活动进行中！",false,false);
            case 2:
                //!!!!!!!!!!!!!!!!
                //!!!!!!!!!!!强制显示当前助力的信息
                return initSharingActivityInfo(saItem,id,saItem.getSubject()+"活动已经成功！",false,true);
            default:
                return initSharingActivityInfo(saItem,id,saItem.getSubject()+"活动未成功！",true,false);
        }
    }

    /**
     * @param mobile
     * @param password
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ClientUserEntity userRegistrationViaHelp(String mobile,String password){
        ClientUserEntity clientUserEntity = clientUserService.getByMobile(mobile);
        if(password == null || clientUserEntity != null){
            return   clientUserEntity;
        }

        ClientUserEntity prospectiveUser = new ClientUserEntity();
        prospectiveUser.setMobile(mobile);
        prospectiveUser.setLevel(1);
        prospectiveUser.setUsername(mobile);
        String signedPassword = DigestUtils.sha256Hex(password);
        prospectiveUser.setPassword(signedPassword);
        prospectiveUser.setCreateDate(new Date());

        try{
            clientUserService.insert(prospectiveUser);
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        ClientUserEntity newUser = clientUserService.getUserByPhone(mobile);
        if(newUser == null)
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        try{
            tokenService.createToken(newUser.getId());
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        return newUser;
    }

    /**
     * @param position 0:本月月初时间，非自本月起后的某个月的月末时间
     * @return
     */
    public Date getSpecifyMonthDate(int position) throws ParseException {
        if(position >11) position = 11;
        Date date = new Date();
        String dhms = "-01 00:00:00";
        if(position == 0){
            String result = TimeUtil.sdfYm.format(date)+dhms;
            return TimeUtil.simpleDateFormat.parse(result);
        }else{

            int res = Integer.parseInt(TimeUtil.sdfM.format(date))+position;
            String year = TimeUtil.sdfYear.format(date);
            if(res>12){
                res-= 12;
                year = Integer.parseInt(year)+1+"";
            }
            String month = res<10?"0"+res:res+"";
            String  result = year+"-"+month+dhms;
            return new Date(TimeUtil.simpleDateFormat.parse(result).getTime()-1000*60*60*24);
        }
    }
    /**
     * @return 检查用户助力次数是否允许
     *
     */
    public boolean allowUserHelp(String mobile,Integer saId,Integer proposeId,
                                 int allowHelpedTimes,int months,Long initiatorId) throws ParseException {

        int helpedCount = sharingActivityLogService.getHelpedCount(mobile,saId,getSpecifyMonthDate(0), getSpecifyMonthDate(months));

        //1超出规定时间表内的助力次数
        if(allowHelpedTimes<=helpedCount)
            return false;

        //2对于同一个活动每个人只能助力一次
        int countOfCurrentSharing = sharingActivityLogService.getHelpedCountForCurrentSharing(initiatorId, saId, proposeId, mobile);
        if(countOfCurrentSharing == 0)
            return true;
        return false;
    }


    /**
     * @return 助力身份认证,返回允许助力的用户信息
     */
    public int helperIdentification(Long initiatorId,String mobile,String password,Integer saId,
                                                   Integer proposeId,boolean activityInitiatorEffective,
                                                   SharingAndDistributionParamsEntity entity) throws ParseException{

        int allowHelpedTimes = entity.getHelpedTimes();      //1-1规定相对时间内允许助力的次数
        int months = entity.getMonths();                     //1-2规定时间：月数
        boolean alwaysRegisterSuccess = entity.getAlwaysRegisterSuccess()==1?true:false;

        ClientUserEntity clientUserEntityDb = clientUserService.getUserByPhone(mobile);
        if(clientUserEntityDb != null){

            if(allowUserHelp(mobile,saId,proposeId,allowHelpedTimes,months,initiatorId)){       //助力次数不超限
                return 0;
            }else{
                return 1;                                                                       //助力次数超限
            }
        }else{
            if(alwaysRegisterSuccess || activityInitiatorEffective){
                ClientUserEntity proSpectiveUser = userRegistrationViaHelp(mobile,password);
                if(proSpectiveUser != null){
                    return 0;
                }else{
                    return 2;   //注册失败，发生错误,请稍的重试
                }
            }else{
                return 3;       //活动已结束且活动结束不接授注册
            }
        }
    }
    /**
     * @param
     * @return
     */

    public Result initResult(ClientUserEntity clientUser,SharingActivityEntity saItem, String msg, boolean isError,boolean isHelper) throws ParseException {
        Map<String,Object> map = new HashMap();
        Result result = new Result();
        if(isError){
            result.setCode(501);
        }else{
            result.setCode(200);
        }
        result.setMsg(msg);

        if(clientUser == null){
            map.put("client_id",null);
            map.put("token",null);
            map.put("initiator_head_img",null);
            map.put("client_name",null);
            map.put("helpers",null);
            map.put("helperReward",null);
            map.put("finishStamp",null);         //增加时间戮
            map.put("initiatorEntity",null);

            result.setData(map);
            return result;
        }
        Long clientId = clientUser.getId();
        String token = tokenService.getByUserId(clientUser.getId()).getToken();
        String headImg = clientUser.getHeadImg();
        String clientName = clientUser.getUsername();
        if(clientName== null)
            clientName = clientUser.getMobile();

        if(saItem == null){
            map.put("client_id",clientId);
            map.put("token",token);
            map.put("initiator_head_img",headImg);
            map.put("client_name",clientName);
            map.put("helpers",null);
            map.put("helperReward",null);
            map.put("finishStamp",null);         //增加时间戮
            map.put("initiatorEntity",null);

            result.setData(map);
            return result;
        }

        Integer saId = saItem.getSaId();
        Integer sharingMethod = saItem.getSharingMethod();
        Integer helpersNum = saItem.getHelpersNum();

        SharingInitiatorEntity sharingInitiator =  sharingInitiatorService.getCurrentOne(clientUser.getId(), saId);
        if(sharingInitiator == null){
            map.put("client_id",clientId);
            map.put("token",token);
            map.put("initiator_head_img",headImg);
            map.put("client_name",clientName);
            map.put("helpers",null);
            map.put("helperReward",null);
            map.put("finishStamp",null);         //增加时间戮
            map.put("initiatorEntity",null);

            result.setData(map);
            return result;
        }
        List<SharingActivityHelpedEntity> helpedListCombo = null;
        Long initiatorId = sharingInitiator.getInitiatorId();
        Integer activityId = sharingInitiator.getSaId();
        Date date = sharingInitiator.getFinishedTime();
        String finishStamp = TimeUtil.dateToStamp(date);

        if(helpersNum != 0){//非发起者，

            helpedListCombo = getHelperList(sharingInitiator,sharingMethod,helpersNum,isHelper);
        }

        Integer helperRewardAmount = 1;
                SharingActivityExtendsEntity extendsInfoById = sharingActivityExtendsService.getExtendsInfoById(saId);
        if(extendsInfoById != null) {
            helperRewardAmount = extendsInfoById.getHelperRewardAmount();
        }
        map.put("client_id",clientId);
        map.put("token",token);
        map.put("initiator_head_img",headImg);
        map.put("client_name",clientName);
        map.put("helpers",helpedListCombo);
        map.put("helperReward",helperRewardAmount);
        map.put("finishStamp",finishStamp);         //增加时间戮
        map.put("initiatorEntity",sharingInitiator);

        result.setData(map);
        return result;
    }


    @PostMapping("helped")
    @ApiOperation("检查是否为本活动助力过")
    public Result isProposer(@RequestBody HelpSharingActivityVo vo) throws Exception{
        Result result = new Result();

        Long initiatorId = vo.getInitiator_id();     //发起者client_usr/id
        Integer saId = vo.getSa_id();               //活动id
        String mobile = vo.getMobile();

        if(mobile == null ||mobile == ""|| initiatorId == null || saId == null){
            System.out.println("request_params(mobile,initiatorId,saId):"+mobile+","+initiatorId+","+saId);
            result.setCode(200);
            result.setMsg("0");
            return result;
        }
        ClientUserEntity clientUserEntity = clientUserService.getClientUser(initiatorId);
        if(clientUserEntity == null){
            result.setCode(200);
            result.setMsg("0");
            return result;
        }


        SharingAndDistributionParamsEntity sharingDistributionParams = sharingAndDistributionParamsService.getSharingDistributionParams();
        if(sharingDistributionParams == null){
            sharingDistributionParams = new SharingAndDistributionParamsEntity();
        }

        SharingInitiatorEntity inProcess = sharingInitiatorService.getLastInProcessOne(initiatorId,saId);
        if(inProcess != null){
            int code = helperIdentification(initiatorId,mobile, null, saId, inProcess.getProposeId(), true, sharingDistributionParams);
            if(code == 1){
                result.setCode(200);
                result.setMsg("1010");
                return result;
            }
        }

        result.setCode(200);
        result.setMsg("0");
        return result;

    }
    /**
     * 协助助力的用户信息(参数方式)
     * @return
     */
    @PostMapping("p_helper")
    @ApiOperation("帮好友助力")
    public Result helpRelayParam(@RequestBody HelpSharingActivityVo vo) throws Exception {
        Result result = new Result();
        result.setCode(501);
        String messageStr = null;

        Long initiatorId = vo.getInitiator_id();    //发起者client_usr/id
        Integer saId = vo.getSa_id();               //活动id
        String mobile = vo.getMobile();
        String password = vo.getPassword();

        if(mobile == null ||mobile == ""|| initiatorId == null || saId == null){
            System.out.println("request_params(mobile,initiatorId,saId):"+mobile+","+initiatorId+","+saId);
            return initResult(null,null,"错误：参数有误！！",true,false);
        }
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, true);

        //0,是否为发起助力者本人
        ClientUserEntity clientUserEntity = clientUserService.getClientUser(initiatorId);
        if(clientUserEntity != null){
            if(clientUserEntity.getMobile().equals(mobile)){
                //自己不能给自己助力
                return initResult(clientUserEntity,saItem,"邀请更多好友为我助力！！",true,false);
            }
        }
//        else{
//            return initResult(null,saItem,"发起者id有误！！",true,false);
//        }

        //1,系统参数
        SharingAndDistributionParamsEntity sharingDistributionParams = sharingAndDistributionParamsService.getSharingDistributionParams();
        if(sharingDistributionParams == null){
            sharingDistributionParams = new SharingAndDistributionParamsEntity();
        }
        boolean alwaysRegisterSuccess = sharingDistributionParams.getAlwaysRegisterSuccess()==1?true:false;

        //2,检查活动是否有效

        if(saItem == null) {//活动已失效

            if(alwaysRegisterSuccess){
                ClientUserEntity proSpectiveUser = userRegistrationViaHelp(mobile,password);
                if(proSpectiveUser != null)
                    return initResult(clientUserEntity,saItem,"本活动已结束，感谢参与!",true,false);
                return initResult(clientUserEntity,saItem,"服务器繁忙，请稍候再试！",true,false);
            }else{
                return initResult(clientUserEntity,saItem,"本活动已结束，感谢参与!",true,false);
            }
        }

        Integer allowHelpersNum = saItem.getHelpersNum();//助力最低人数
        Integer allPersonLimit = saItem.getPersonLimit();//最大助力次数
        Integer sharingMethod = saItem.getSharingMethod();//助力方式
        Integer helpersNum = saItem.getHelpersNum();

        //3,用户是否发起了此活动(活动状态为1，若活动状态没有为1的则为2的)
        SharingInitiatorEntity inProcess = sharingInitiatorService.getLastInProcessOne(initiatorId,saId);
        if(inProcess == null) {//用户未发起或助力已完成
            if(alwaysRegisterSuccess){
                ClientUserEntity proSpectiveUser = userRegistrationViaHelp(mobile,password);
                if(proSpectiveUser != null)
                    return initResult(clientUserEntity,saItem,"用户助力活动已完成！",true,false);
                return initResult(clientUserEntity,saItem,"服务器繁忙，请稍候再试！",true,false);
            }else{
                return initResult(clientUserEntity,saItem,"点晚了，活动已完成！",true,false);
            }
        }

        //4,活动有效-->进行用户授权及用户助力次数检查
        int code = helperIdentification(initiatorId,mobile, password, saId, inProcess.getProposeId(), true, sharingDistributionParams);
        switch(code){
            case 0:
                String masterMobile = null;
                ClientUserEntity clientUserTmp = clientUserService.getClientUser(initiatorId);
                if(clientUserTmp != null)
                    masterMobile = clientUserTmp.getMobile();
                if(masterMobile != null){
                    if(masterMobile != mobile){
                        distributionRewardService.binding(saId,masterMobile,mobile);//绑定主从关系后执行后续工作;
                    }else{
                        return initResult(clientUserEntity,saItem,"您的助力正在进行中!",true,false);//以后改成查看助力详情
                    }
                }else{
                    return initResult(clientUserEntity,saItem,"繁忙或异常(0)，请稍后重试!",true,false);
                }

                break;
            case 1://次数超限
                return initResult(clientUserEntity,saItem,"助力次数超限，晚点再试吧!",true,false);
            case 2://注册失败
                return initResult(clientUserEntity,saItem,"繁忙或异常(1)，请稍后重试!",true,false);
            case 3://活动结束且非都可注册成功模式
                return initResult(clientUserEntity,saItem,"本活动已结束，感谢参与!",true,false);
        }

//======================================================================================================================
        //5,更新奖励======================
        Integer completeCount = sharingActivityLogService.getCount(initiatorId, saId,inProcess.getProposeId());

        SharingActivityExtendsEntity extendsInfo = sharingActivityExtendsService.getExtendsInfoById(saItem.getSaId());
        int rValue = 0;
        if((completeCount+1)<allowHelpersNum){//助力未成功
            //平台发布的助力活动
            int rewardSum = sharingActivityLogService.getRewardSum(initiatorId,saId,inProcess.getProposeId());
            int remainCount = inProcess.getRewardValue()- rewardSum - allowHelpersNum + completeCount+1;
            int itemReward =1;
            if(rewardSum < inProcess.getRewardValue()){//肋力的值
                 itemReward = new  SharingActivityRandomUtil(remainCount).getRandomValue();
            }
            //给助力者发奖励
            insertSharingActivityLog(saId,initiatorId,mobile,itemReward,inProcess.getProposeId());
            messageStr = "助力成功："+itemReward+saItem.getRewardUnit()+"!";
            prizesHelper(mobile,extendsInfo.getHelperRewardType(),extendsInfo.getHelperRewardAmount());

        }else{
            //助力活动达限或超限
            //-------------------------------------------------------------------------------------
            if(inProcess.getStatus() == ESharingInitiator.IN_PROCESSING.getCode()){

                messageStr = "助力成功,发起者获得：" + saItem.getRewardAmount() + saItem.getRewardUnit() + "!";

                int finalReward = 0;//更新助力记录：获得还需助力的费用值
                int rewardSum = sharingActivityLogService.getRewardSum(initiatorId, saId, inProcess.getProposeId());
                if (rewardSum < saItem.getRewardAmount()) {
                    finalReward = saItem.getRewardAmount() - rewardSum;
                }
                //更新
                inProcess.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());                          //更新发起者助力状态
                sharingInitiatorService.updateById(inProcess);                                              //重新更新表格状态
                insertSharingActivityLog(saId, initiatorId, mobile, finalReward, inProcess.getProposeId());                     //更新助力记录：获得还需助力的费用值
                prizesInitiator(saItem, clientUserEntity);                                                                           //给发起者奖励
                prizesHelper(mobile, extendsInfo.getHelperRewardType(),extendsInfo.getHelperRewardAmount());  //给助力用户发奖励
                messageStr =  getHelpedSuccessMessage(extendsInfo);                          //更新成功消息

            }else if(completeCount <allPersonLimit && sharingMethod == ESharingActivity.SharingMethod.INFINITE_METHOD.getCode()){//无限模式下允许用户仍可获得奖励

                insertSharingActivityLog(saId, initiatorId, mobile, 0, inProcess.getProposeId());                     //更新助力记录：获得还需助力的费用值
                prizesHelper(mobile, extendsInfo.getHelperRewardType(),extendsInfo.getHelperRewardAmount());  //给助力用户发奖励
                messageStr =  getHelpedSuccessMessage(extendsInfo);                          //更新成功消息

            }else{
                return initResult(clientUserEntity,saItem,"手慢了，本次活动已经完成！",true,false);
            }
        }

        return initResult(clientUserEntity,saItem,messageStr,false,true);
    }

    public String getHelpedSuccessMessage(SharingActivityExtendsEntity extendsInfo) throws Exception{

        String unitStrng = "代付金";
        switch(extendsInfo.getHelperRewardType()){
            case 1://代付金额
                unitStrng = "代付金";
                //帮得10元代付金，发起成功得50元代付金
                break;
            case 2://商品
                unitStrng = "件";
                break;
            case 3://菜品
                unitStrng = "盘";
                break;
            case 4://宝币
                unitStrng = "宝币";
                break;
        }
        return "助力成功，恭喜获得"+extendsInfo.getHelperRewardAmount()+unitStrng+"!";
    }

    //更新记录助力记录
    private void insertSharingActivityLog(int activityId,long initaiatorId,String helperMobile,int helpValue,int proposeSequeueNo){
        SharingActivityLogEntity sharingActivityLogEntity = new SharingActivityLogEntity();
        sharingActivityLogEntity.setActivityId(activityId);
        sharingActivityLogEntity.setInitiatorId(initaiatorId);
        sharingActivityLogEntity.setHelperMobile(helperMobile);
        sharingActivityLogEntity.setHelperValue(helpValue);
        sharingActivityLogEntity.setProposeSequeueNo(proposeSequeueNo);

        sharingActivityLogService.insertOne(sharingActivityLogEntity);
    }

    //给发起者发奖金
    private void prizesInitiator(SharingActivityEntity sharingActivityEntity,ClientUserEntity initiator){
        Long initiatorId = initiator.getId();
        switch(sharingActivityEntity.getRewardType()){
            case 1://代付金
                Integer gift = sharingActivityEntity.getRewardAmount();
                updateBalanceRecord(initiator,gift,4);
                break;

            case 3://奖励菜品    怎样给商家展示或者到商家使用
                SharingRewardGoodsRecordEntity rewardGoodsentity = new SharingRewardGoodsRecordEntity();
                rewardGoodsentity.setActivityId(sharingActivityEntity.getSaId());
                rewardGoodsentity.setClientId(initiatorId);
                rewardGoodsentity.setGoodsId(sharingActivityEntity.getRewardId());
                rewardGoodsentity.setMerchantId(sharingActivityEntity.getRewardMchId());

                //过期时间属于需要添加的字段---默认为活动结束时间
                rewardGoodsentity.setExpireTime(sharingActivityEntity.getCloseDate());
                rewardGoodsentity.setUpdatePmt(new Date());
                rewardGoodsentity.setGoodsNum(sharingActivityEntity.getRewardAmount());
                rewardGoodsentity.setStatus(ESharingRewardGoods.REWARD_ENABLE.getCode());

                sharingRewardGoodsRecordService.insertItem(rewardGoodsentity);
                break;
            case 4://宝币
                Integer balance = sharingActivityEntity.getRewardAmount();
                updateBalanceRecord(initiator,balance,4);
                break;

        }
    }

    //给助力者发奖金
    private void prizesHelper(String mobile,int rewardType,int rewardValue){

        ClientUserEntity clientUser = clientUserService.getByMobile(mobile);
        if(clientUser == null)
            return;
        Long id = clientUser.getId();
        switch(rewardType)
        {
            case 1://代付金
                updateBalanceRecord(clientUser,rewardValue,1);
                break;
            case 4://宝币
                updateBalanceRecord(clientUser,rewardValue,4);
                break;
        }
    }


    public void updateBalanceRecord(ClientUserEntity client,Integer value,int type){

        if(type == 4)//宝币
        {
            BigDecimal resBalance = client.getBalance();
            //奖励值大于0并且原始值小于宝币限值
            if(value >= 0 && resBalance.compareTo(balanceLimit)<0){

                if((resBalance.add(new BigDecimal(value+""))).compareTo(balanceLimit) >= 0){
                    client.setBalance(balanceLimit);
                    clientUserDao.updateById(client);

                    BigDecimal amount = new BigDecimal("0");
                    amount = balanceLimit.subtract(resBalance);
                    //代付金为6宝币为13
                    addRecordFromRecordGift(client,13,amount,balanceLimit);


                }else{
                    clientUserService.addBalanceByUserid(client.getId()+"",value+"");
                    //代付金为6宝币为13
                    BigDecimal balance = new BigDecimal(value+"");
                    balance = resBalance.add(balance);
                    addRecordFromRecordGift(client,13,new BigDecimal(value),balance);
                }
            }
        }else if(type == 1)
        {
            if(value >= 0)
                clientUserService.addRecordGiftByUserid(client.getId()+"",value+"");

            //更新调用参数
            BigDecimal bd1 = new BigDecimal(value + "");
            BigDecimal balance = client.getGift();
            balance = balance.add(bd1);

            //代付金为6宝币为13
            addRecordFromRecordGift(client,6,bd1,balance);
        }
    }

    public void addRecordFromRecordGift(ClientUserEntity client,int status,BigDecimal amount,BigDecimal balance){
        RecordGiftEntity recordGiftEntity = new RecordGiftEntity();
        recordGiftEntity.setUserId(client.getId());//受益人
        recordGiftEntity.setUseGift(amount);
        recordGiftEntity.setBalanceGift(balance);
        recordGiftEntity.setStatus(status);
        recordGiftEntity.setCreateDate(new Date());
        recordGiftDao.insert(recordGiftEntity);
    }
    //onlyEnabled是否仅查询有效的助力活动（客户，时间）
    @GetMapping("list")
    @ApiOperation("助力列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idOfClientUser", value = "发起者Id",required = true, paramType = "query", dataType="long"),
            @ApiImplicitParam(name = "onlyEnabled", value = "1仅有效的，0所有的",required = true, paramType = "query", dataType="int")
    })
    public Result getAllSharingActivity(Long idOfClientUser,Integer onlyEnabled){
        List<SharingActivityEntity> list = null;
        //时间有效的助力活动
        if(onlyEnabled == 1){
            list = sharingActivityService.getList(true);
        }else{
            list = sharingActivityService.getList(false);
        }

        Result result = new Result();

        if(list != null){
            result.setCode(200);
            result.setData(list);
        }else{
            result.setCode(404);
        }

        result.setMsg("成功");
        return result;
    }

    @PostMapping("mch_propose_sa")
    @ApiOperation("商家发布助力活动")
    public Result mchProposeSharingActivity(@RequestBody SharingActivityDTO sharingActivityDTO){
        List<SharingActivityEntity> oneByMerchantIdAndStatus = sharingActivityService.getListByMerchantIdAndStatus(sharingActivityDTO.getRewardMchId(),null);
        if (oneByMerchantIdAndStatus.size()>0){
            return new Result().error("您有助力活动未结束");
        }
        SharingActivityEntity sharingActivityEntity = new SharingActivityEntity();
        sharingActivityEntity.setActivityImg(sharingActivityDTO.getActivityImg());
        sharingActivityEntity.setSubject(sharingActivityDTO.getSubject());
        sharingActivityEntity.setHelpersNum(sharingActivityDTO.getHelpersNum());
        sharingActivityEntity.setMemberHelper(sharingActivityDTO.getMemberHelper());
        sharingActivityEntity.setSuccessMsg(sharingActivityDTO.getSuccessMsg());
        sharingActivityEntity.setProposeTimes(sharingActivityDTO.getProposeTimes());
        sharingActivityEntity.setRepeatableTimes(sharingActivityDTO.getRepeatableTimes());
        sharingActivityEntity.setRewardType(3);
        sharingActivityEntity.setRewardAmount(sharingActivityDTO.getRewardAmount());
        sharingActivityEntity.setRewardId(sharingActivityDTO.getRewardId());
        sharingActivityEntity.setRewardMchId(sharingActivityDTO.getRewardMchId());
        sharingActivityEntity.setInStoreOnly(sharingActivityDTO.getInStoreOnly());
        sharingActivityEntity.setRewardUnit(sharingActivityDTO.getRewardUnit());

        sharingActivityEntity.setHelperSuccess(sharingActivityDTO.getHelperSuccess());
        sharingActivityEntity.setWinningWords(sharingActivityDTO.getWinningWords());
        sharingActivityEntity.setOpenDate(sharingActivityDTO.getOpenDate());
        sharingActivityEntity.setCloseDate(sharingActivityDTO.getCloseDate());
        sharingActivityService.insertOne(sharingActivityEntity);
        return new Result().ok("生成成功！");
    }

    /**
     * 获取商家助力活动列表内容
     * @param merchantId
     * @return
     */
    @GetMapping("mch_sa_list")
    @ApiOperation("根据商家获取去助力")
    public Result listByMerchant(long merchantId){
        List<SharingActivityEntity> list = sharingActivityService.getListByMerchantIdAndStatus(merchantId,null);
        if (list.size()>0){
            return new Result().ok(list);
        }else {
            return new Result().ok("没有助力信息");
        }
    }
}

