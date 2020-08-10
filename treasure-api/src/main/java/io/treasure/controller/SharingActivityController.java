package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dto.SharingActivityDTO;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.*;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.*;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.ProposeSharingActivityVo;
import io.treasure.vo.HelpSharingActivityVo;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/sharing_activity")
@Api(tags="助力活动")
public class SharingActivityController {

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

    @PostMapping("startRelay")
    @ApiOperation("发起助力")
    public Result startRelay(@RequestBody ProposeSharingActivityVo vo) throws Exception{

        Long id = vo.getId();
        Integer saId = vo.getSaId();
        System.out.println("get requestBody info(id,said):"+id+","+saId);
        //1、检查活动是否存在(只输入活动id)
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, false);
        if(saItem == null){
            return new Result().error("活动("+saId+")不存在！");
        }else{
            if(saItem.getCloseDate().getTime()<= new Date().getTime()){
                return new Result().error(saItem.getSubject()+"活动已结束！");
            }
        }

        //2、初始化数据
        SharingInitiatorEntity siEntity = new SharingInitiatorEntity();
        siEntity.setSaId(saId);
        siEntity.setInitiatorId(id);
        siEntity.setRewardType(saItem.getRewardType());
        siEntity.setRewardValue(saItem.getRewardAmount());
        siEntity.setStatus(ESharingInitiator.IN_PROCESSING.getCode());
        siEntity.setStartTime(saItem.getOpenDate());
        siEntity.setFinishedTime(saItem.getCloseDate());
        //初始化二维码
        //siEntity.setQrCode(initQRCode(id+""));

        Result result = new Result();
        Map<String,Object> map = new HashMap<>();

        Integer helpersNum =  saItem.getHelpersNum();
        Integer rewardAmount = saItem.getRewardAmount();
        map.put("helpersNum",helpersNum);
        map.put("rewardAmount",rewardAmount);

        List<SharingInitiatorEntity> unreadedLogList = sharingActivityLogService.getUnreadedLogList(id, saId);

        //增加查看列表
        List<SharingActivityHelpedEntity> helpedListCombo = getHelpedListCombo(id, saId);
        if(helpedListCombo.size() == 0){
            map.put("helpers",helpedListCombo);
        }else{
            List<SharingActivityHelpedEntity> helpedListComboUnread = getHelpedListComboUnread(id, saId);
            map.put("helpers",helpedListComboUnread);
        }
        map.put("body",saItem.getHelpersNum());

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

        SharingInitiatorEntity sharingInitiatorEntity = sharingInitiatorService.insertOne(siEntity);
        if(sharingInitiatorEntity!= null){
            map.put("sharing",sharingInitiatorEntity);
            String finishStamp = TimeUtil.dateToStamp(saItem.getCloseDate());
            map.put("finishStamp",finishStamp);
            result.setMsg("成功发起："+saItem.getSubject()+"活动");
            result.setData(map);
            result.setCode(200);

            return result;
        }else{
            map.put("sharing",null);
            map.put("finishStamp",null);
            map.put("initiator_head_img",headImage);
            map.put("helpers",null);

            result.setData(map);
            return result.error("活动发起失败，请稍后重试！");
        }
    }

    public String initQRCode(String client_id)throws Exception{

        String url ="https://jubaoapp.com:8443";
        Map<String,String> map = new HashMap<>();
        map.put("id",client_id);
        //map.put("saId","777");
        return qrCodeService.generateQrAndUrl(url,map);
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
//=====================================================================================================

    public Result initSharingActivityInfo(SharingActivityEntity saItem,Long initiatorId,int saId,String msg,boolean isError,int helperNum)throws Exception{
        Result result = new Result();
        Map<String,Object> map = new HashMap<>();

        if(isError){
            result.setCode(501);
        }else{
            result.setCode(200);
        }

        result.setMsg(msg);//设置返回信息

        //发起者基本信息中添加头像
        ClientUserEntity clientUser = clientUserService.getClientUser(initiatorId);
        String headImage = null;
        String client_name = null;
        if(clientUser != null){
            headImage = clientUser.getHeadImg();
            client_name = clientUser.getUsername();
            if(client_name== null)
                client_name = clientUser.getMobile();
        }

        map.put("initiator_head_img",headImage);
        map.put("client_name",client_name);
        //助力活动相关信息
        SharingInitiatorEntity currentOne = sharingInitiatorService.getCurrentOne(initiatorId, saId);

        System.out.println("当前是否已读状态"+currentOne.getReaded()+","+currentOne.getProposeId());
        if(currentOne.getReaded() == 1){
            //增加查看列表
            List<SharingActivityHelpedEntity> helpedListCombo = getHelpedListComboByProposeId(initiatorId, saId,currentOne.getProposeId());
            if(helpedListCombo != null)
                map.put("helpers",helpedListCombo);
        }else{
            map.put("helpers",null);
        }
        String finishStamp = TimeUtil.dateToStamp(currentOne.getFinishedTime());

        Integer helpersNum =  null;
        Integer rewardAmount = null;
        if(saItem != null){
            helpersNum =  saItem.getHelpersNum();
            rewardAmount = saItem.getRewardAmount();
        }

        map.put("helpersNum",helpersNum);
        map.put("rewardAmount",rewardAmount);

        map.put("finishStamp",finishStamp);
        map.put("body",helperNum);
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
            return initSharingActivityInfo(null,id,saId,"活动("+saId+")不存在！",true,0);
        }else{
            helpersNum = saItem.getHelpersNum();
            if(saItem.getCloseDate().getTime()<= new Date().getTime()){
                return initSharingActivityInfo(saItem,id,saId,saItem.getSubject()+"活动已结束！",true,helpersNum);
            }

        }
        //2、初始化数据
        val currentOne = sharingInitiatorService.getCurrentOne(vo.getId(), vo.getSaId());
        switch(currentOne.getStatus()){
            case 1:
                return initSharingActivityInfo(saItem,id,saId,saItem.getSubject()+"活动进行中！",false,helpersNum);
            case 2:
                return initSharingActivityInfo(saItem,id,saId,saItem.getSubject()+"活动已经成功！",false,helpersNum);

            default:
                return initSharingActivityInfo(saItem,id,saId,saItem.getSubject()+"活动未成功！",true,helpersNum);

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

        int helpedCount = sharingActivityLogService.getHelpedCount(mobile, getSpecifyMonthDate(0), getSpecifyMonthDate(months));

        //1超出规定时间表内的助力次数
        if(allowHelpedTimes<=helpedCount)
            return false;


        //2对于同一个活动每个人只能助力一次
        int countOfCurrentSharing = sharingActivityLogService.getHelpedCountForCurrentSharing(initiatorId, saId, proposeId, mobile);
        if(countOfCurrentSharing == 0)
            return true;
        return false;
        //if(sharingActivityLogService.getOne(saId,proposeId,mobile) != null)
        //  return false;
        //return true;


    }


    /**
     * @return 助力身份认证,返回允许助力的用户信息
     */
    public int helperIdentification(Long initiatorId,String mobile,String password,
                                                   Integer saId,
                                                   Integer proposeId,
                                                   boolean activityInitiatorEffective,
                                                   SharingAndDistributionParamsEntity entity
    ) throws ParseException{

        int allowHelpedTimes = entity.getHelpedTimes();      //1-1规定相对时间内允许助力的次数
        int months = entity.getMonths();                     //1-2规定时间：月数
        boolean alwaysRegisterSuccess = entity.getAlwaysRegisterSuccess()==1?true:false;

        ClientUserEntity clientUserEntityDb = clientUserService.getUserByPhone(mobile);
        if(clientUserEntityDb != null){

            //助力次数不超限
            if(allowUserHelp(mobile,saId,proposeId,allowHelpedTimes,months,initiatorId)){
                return 0;
            }else{
                return 1;//助力次数超限
            }

        }else{
            if(alwaysRegisterSuccess || activityInitiatorEffective){
                ClientUserEntity proSpectiveUser = userRegistrationViaHelp(mobile,password);
                if(proSpectiveUser != null){
                    return 0;
                }else{
                    return 2;//注册失败，发生错误,请稍的重试
                }

            }else{
                return 3;//活动已结束且活动结束不接授注册
            }
        }
    }
    /**
     * @param errorMessage
     * @return
     */

    public Result initResult(String errorMessage, String helperMobile, boolean isError,
                            Long intitiatorId,Integer activityId,SharingInitiatorEntity sharingInitiatorEntity,int rewardValue) throws ParseException {
        Map<String,Object> map = new HashMap();
        Result result = new Result();
        if(isError){
            result.setCode(501);
        }else{
            result.setCode(200);
            if(rewardValue != 0)
                map.put("helperReward",rewardValue);
        }
        ClientUserEntity clientUser = clientUserService.getByMobile(helperMobile);
        if(clientUser != null){
            map.put("client_id",clientUser.getId());
            map.put("token",tokenService.getByUserId(clientUser.getId()).getToken());
            map.put("initiator_head_img",clientUser.getHeadImg());
            String clientName = clientUser.getUsername();
            if(clientName== null)
                clientName = clientUser.getMobile();
            map.put("client_name",clientName);
        }else{
            map.put("client_id",null);
            map.put("token",null);
            map.put("initiator_head_img",null);
            map.put("client_name",null);
        }
        System.out.println("sa_info:"+errorMessage);
        map.put("msg",errorMessage);

        //增加查看列表
        if(intitiatorId != null && activityId != null){
            List<SharingActivityHelpedEntity> helpedListCombo = getHelpedListComboUnread(intitiatorId, activityId);
            if(helpedListCombo != null)
                map.put("helpers",helpedListCombo);
        }


        String finishStamp = null;
        //查询活动值
        if(sharingInitiatorEntity == null){
            sharingInitiatorEntity= sharingInitiatorService.getLastInProcessOne(intitiatorId,activityId);
            if(sharingInitiatorEntity != null){
                Date date = sharingInitiatorEntity.getFinishedTime();
                finishStamp = TimeUtil.dateToStamp(date);
            }
        }else{
            Date date = sharingInitiatorEntity.getFinishedTime();
            finishStamp = TimeUtil.dateToStamp(date);
        }

        map.put("finishStamp",finishStamp);//增加时间戮
        map.put("initiatorEntity",sharingInitiatorEntity);

        result.setData(map);

        return result;
    }
    @PostMapping("helped")
    @ApiOperation("检查是否为本活动助力过")
    public Result isProposer(@RequestBody HelpSharingActivityVo vo) throws Exception{
        Result result = new Result();

        String messageStr = null;
        Long initiatorId = vo.getInitiator_id();    //发起者client_usr/id
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
        long creator = 0;//0表示平台发出
        //Map map = new HashMap();
        String messageStr = null;

        Long initiatorId = vo.getInitiator_id();    //发起者client_usr/id
        Integer saId = vo.getSa_id();               //活动id
        String mobile = vo.getMobile();
        String password = vo.getPassword();

        if(mobile == null ||mobile == ""|| initiatorId == null || saId == null){
            System.out.println("request_params(mobile,initiatorId,saId):"+mobile+","+initiatorId+","+saId);
            return initResult("错误：参数有误！！",mobile,true,initiatorId,saId,null,0);
        }
        //0,是否为发起助力者本人
        ClientUserEntity clientUserEntity = clientUserService.getClientUser(initiatorId);
        if(clientUserEntity != null)
            if(clientUserEntity.getMobile().equals(mobile)){
                //自己不能给自己助力
                return initResult("邀请更多好友为我助力！！",mobile,true,initiatorId,saId,null,0);
            }

        //1,系统参数
        SharingAndDistributionParamsEntity sharingDistributionParams = sharingAndDistributionParamsService.getSharingDistributionParams();
        if(sharingDistributionParams == null){
            sharingDistributionParams = new SharingAndDistributionParamsEntity();
        }
        boolean alwaysRegisterSuccess = sharingDistributionParams.getAlwaysRegisterSuccess()==1?true:false;

        //2,检查活动是否有效
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, true);
        if(saItem == null) {//活动已失效
            if(alwaysRegisterSuccess){
                ClientUserEntity proSpectiveUser = userRegistrationViaHelp(mobile,password);
                if(proSpectiveUser != null)
                    return initResult("本活动已结束，感谢参与!",mobile,true,initiatorId,saId,null,0);
                return initResult("服务器繁忙，请稍候再试！",null,true,initiatorId,saId,null,0);
            }else{
                return initResult("本活动已结束，感谢参与!",null,true,initiatorId,saId,null,0);
            }
        }

        creator = saItem.getCreator();
        //3,用户是否发起了此活动(活动状态为1，若活动状态没有为1的则为2的)
        SharingInitiatorEntity inProcess = sharingInitiatorService.getLastInProcessOne(initiatorId,saId);
        if(inProcess == null) {//用户未发起或助力已完成
            if(alwaysRegisterSuccess){
                ClientUserEntity proSpectiveUser = userRegistrationViaHelp(mobile,password);
                if(proSpectiveUser != null)
                    return initResult("用户助力活动已完成！",mobile,true,initiatorId,saId,inProcess,0);
                return initResult("服务器繁忙，请稍候再试！",null,true,initiatorId,saId,inProcess,0);
            }else{
                return initResult("点晚了，活动已完成！",null,true,initiatorId,saId,inProcess,0);
            }
        }

        //4,活动有效-->进行用户授权及用户助力次数检查
        int code = helperIdentification(initiatorId,mobile, password, saId, inProcess.getProposeId(), true, sharingDistributionParams);
        switch(code){
            case 0:
                //取得用户信息
                //绑定主从关系
                String masterMobile = null;
                ClientUserEntity clientUserTmp = clientUserService.getClientUser(initiatorId);
                if(clientUserTmp != null)
                    masterMobile = clientUserTmp.getMobile();
                if(masterMobile != null){
                    if(masterMobile != mobile){
                        distributionRewardService.binding(saId,masterMobile,mobile);
                        //绑定主从关系后执行后续工作;
                    }else{
                        //以后改成查看助力详情
                        return initResult("您的助力正在进行中!",mobile,true,initiatorId,saId,inProcess,0);
                    }
                }else{
                    return initResult("繁忙或异常(0)，请稍后重试!",mobile,true,initiatorId,saId,inProcess,0);
                }

                break;
            case 1://次数超限
                return initResult("助力次数超限，晚点再试吧!",mobile,true,initiatorId,saId,inProcess,0);
            case 2://注册失败
                return initResult("繁忙或异常(1)，请稍后重试!",null,true,initiatorId,saId,inProcess,0);
            case 3://活动结束且非都可注册成功模式
                return initResult("本活动已结束，感谢参与!",null,true,initiatorId,saId,inProcess,0);
        }

        //5,更新奖励======================
        Integer completeCount = sharingActivityLogService.getCount(initiatorId, saId,inProcess.getProposeId());

        Integer allowHelpersNum = saItem.getHelpersNum();//助力最低人数
        Integer allPersonLimit = saItem.getPersonLimit();//最大助力次数
        SharingActivityExtendsEntity extendsInfo = sharingActivityExtendsService.getExtendsInfoById(saItem.getSaId());
        int rValue = 0;
        if((completeCount+1)<allowHelpersNum){//助力未成功
            //平台发布的助力活动

                int rewardSum = sharingActivityLogService.getRewardSum(initiatorId,saId,inProcess.getProposeId());
                int remainCount = inProcess.getRewardValue()- rewardSum - allowHelpersNum + completeCount+1;
                if(rewardSum < inProcess.getRewardValue()){

                    //int itemReward = new  SharingActivityRandomUtil(inProcess.getRewardValue()-rewardSum).getRandomValue();
                    int itemReward = new  SharingActivityRandomUtil(remainCount).getRandomValue();
                    insertSharingActivityLog(saId,initiatorId,mobile,itemReward,inProcess.getProposeId());

                    //map.put("msg","助力成功："+itemReward+saItem.getRewardUnit()+"!");
                    messageStr = "助力成功："+itemReward+saItem.getRewardUnit()+"!";
                }else{
                    insertSharingActivityLog(saId,initiatorId,mobile,1,inProcess.getProposeId());
                }
                //给用户奖励
                rValue = extendsInfo.getHelperRewardAmount();
                if(rValue > 0)
                    prizesHelper(saItem.getSaId(),mobile,extendsInfo.getHelperRewardType(),rValue);

        }else if((completeCount+1) == allowHelpersNum) {//助力成功
            //if(creator == 0l){
                if(inProcess.getStatus() == ESharingInitiator.IN_PROCESSING.getCode()){

                    messageStr = "助力成功,获得：" + saItem.getRewardAmount() + saItem.getRewardUnit() + "!";

                    //更新助力记录：获得还需助力的费用值
                    int rewardSum = sharingActivityLogService.getRewardSum(initiatorId, saId, inProcess.getProposeId());
                    if (rewardSum < saItem.getRewardAmount()) {
                        int itemReward = saItem.getRewardAmount() - rewardSum;

                        insertSharingActivityLog(saId, initiatorId, mobile, itemReward, inProcess.getProposeId());
                    }

                    //更新发起者助力状态
                    inProcess.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());
                    sharingInitiatorService.updateById(inProcess);//重新更新表格状态

                    prizesInitiator(saItem, initiatorId);//给发起者奖励

                    //给用户奖励???????用户助力分享成功前是否给助力用户发奖励
                    rValue = extendsInfo.getHelperRewardAmount();
                    if (rValue > 0)
                        prizesHelper(saItem.getSaId(), mobile, extendsInfo.getHelperRewardType(), rValue);
               }
               else{
                    return continueSharing(saItem,saId,initiatorId,mobile,inProcess,extendsInfo);
              }

            //}else{
                //给商家奖励
              //  prizesInitiator(saItem, initiatorId);//给发起者奖励
              //  return initResult("恭喜助力成功！",mobile,false,initiatorId,saId,inProcess,0);
            //}
        }else if(completeCount <allPersonLimit){//成功但可继续
            if(creator == 0){
                return continueSharing(saItem,saId,initiatorId,mobile,inProcess,extendsInfo);
            }else{
                return initResult("手慢了，本次活动已经完成！",mobile,true,initiatorId,saId,inProcess,0);
            }
        }else{
            return initResult("手慢了，本次活动已经完成！",mobile,true,initiatorId,saId,inProcess,0);
        }

        return initResult(messageStr,mobile,false,initiatorId,saId,inProcess,rValue);
    }

    public Result continueSharing(SharingActivityEntity saItem,Integer saId,
                                  Long initiatorId,String mobile,
                                  SharingInitiatorEntity inProcess,SharingActivityExtendsEntity extendsInfo) throws Exception{
        //小波动随机数
        int rValue = saItem.getRewardLimit()/saItem.getPersonLimit();

        int itemReward = new  SharingActivityRandomUtil(rValue).getRandomValue();
        insertSharingActivityLog(saId,initiatorId,mobile,itemReward,inProcess.getProposeId());

        prizesHelper(saItem.getSaId(),mobile,extendsInfo.getHelperRewardType(),itemReward);
            /*
            奖励类型1-代付金，2-商品，3-菜品 4--宝币
             */
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
        //map.put("msg","恭喜获得"+rValue+unitStrng+"!");
        return initResult("助力成功，恭喜获得"+rValue+unitStrng+"!",mobile,false,initiatorId,saId,inProcess,itemReward);

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
    private void prizesInitiator(SharingActivityEntity sharingActivityEntity,Long initiatorId){
        switch(sharingActivityEntity.getRewardType()){
            case 1://代付金
                Integer rewardAmount = sharingActivityEntity.getRewardAmount();
                if(rewardAmount >= 0)
                    clientUserService.addRecordGiftByUserid(initiatorId+"",rewardAmount+"");
                break;

            case 3:
                ///////////////////////////////////////////////////////////////////////////////////////////////////////
//sharingRewardGoodsRecordService.insertItem();
                break;
            case 4://宝币
                Integer rewardAmount4 = sharingActivityEntity.getRewardAmount();
                if(rewardAmount4 >= 0)
                    clientUserService.addBalanceByUserid(initiatorId+"",rewardAmount4+"");
                break;

        }
    }
    //给助力者发奖金
    private void prizesHelper(Integer saeId,String mobile,int rewardType,int rewardValue){

        ClientUserEntity byMobile = clientUserService.getByMobile(mobile);
        if(byMobile == null)
            return;
        Long id = byMobile.getId();
        switch(rewardType)
        {
            case 1://代付金
                    clientUserService.addRecordGiftByUserid(id+"",rewardValue+"");
                break;
            case 4://宝币
                    clientUserService.addBalanceByUserid(id+"",rewardValue+"");
                break;
        }

    }

    public List<SharingActivityHelpedEntity> getHelpedListComboByProposeId(Long intitiatorId, Integer activityId,Integer proposeId){
        return sharingActivityLogService.getHelpedListComboByProposeId(intitiatorId,activityId,proposeId);
    }

    public List<SharingActivityHelpedEntity> getHelpedListCombo(Long intitiatorId, Integer activityId){
        return sharingActivityLogService.getHelpedListCombo(intitiatorId,activityId);
    }

    public List<SharingActivityHelpedEntity> getHelpedListComboUnread(Long intitiatorId, Integer activityId){
        return sharingActivityLogService.getHelpedListComboUnread(intitiatorId,activityId);
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

    //==================================================================================================================
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

