package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.*;
import io.treasure.service.*;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.vo.ProposeSharingActivityVo;
import io.treasure.vo.HelpSharingActivityVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SharingInitiatorService sharingInitiatorService;

    @Autowired
    private SharingActivityLogService sharingActivityLogService;

    @Autowired(required = false)
    private SharingActivityLogDao sharingActivityLogDao;

    @PostMapping("startRelay")
    @ApiOperation("发起助力")
    public Result startRelay(@RequestBody ProposeSharingActivityVo vo){

        Long id = vo.getId();
        Integer saId = vo.getSaId();
        System.out.println("get requestBody info(id,said):"+id+","+saId);
        //1、检查活动是否存在
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

        boolean b = sharingInitiatorService.insertOne(siEntity);
        if(b)
            return new Result().ok("成功发起："+saItem.getSubject()+"活动");
        return new Result().error("活动发起失败，请稍后重试！");
    }


    /**
     * 协助助力的用户信息(参数方式)
     * @return
     */
    @PostMapping("p_helper")
    @ApiOperation("帮好友助力")
    public Result helpRelayParam(@RequestBody HelpSharingActivityVo vo) {
        boolean newUserOnly = true;//仅新用户有效
        Result result = new Result();
        result.setCode(501);
        Map map = new HashMap();
        ClientUserEntity newClient = null;

        TokenEntity helperTokenEntity = null;
        String helperClientId = null;

        Long initiatorId = vo.getInitiator_id();    //发起者client_usr/id
        Integer saId = vo.getSa_id();               //活动id

        String mobile = vo.getMobile();
        String password = vo.getPassword();
        String unionid = vo.getUnionid();
        //String clientId = vo.getClientId();

        System.out.println("initiator_id,sa_id,mobile,password:"+initiatorId+","+saId+","+mobile+","+password);
        //System.out.println("unionid,clientId:"+unionid+","+clientId);

        if(mobile == null || password == null || initiatorId == null || saId == null){
            return result.error("错误：参数有误！！");
        }

        /*
            private Long initiator_id;      //发起助力者的：client_user/id
            private Integer sa_id;          //活动编号

            private String mobile;          //新注册：用户的手机号
            private String password;        //新注册：密码
            private String unionid;         //新注册：unionId
            private String clientId;        //新注册：clientId
         */

        //检查是否有token如果没有则插入（只针对新用户，所以完成注册）
        if(newUserOnly){
            if(clientUserService.getUserByPhone(mobile) == null && clientUserService.getLogOffCount(mobile)==0){

                ClientUserEntity user = new ClientUserEntity();
                user.setMobile(mobile);
                user.setUsername(mobile);

                String signedPassword = DigestUtils.sha256Hex(password);
                System.out.println("signedPssword:"+signedPassword);
                user.setPassword(signedPassword);

                user.setCreateDate(new Date());
                //user.setUnionid(unionid);
                //user.setClientId(clientId);
                clientUserService.insert(user);

                newClient = clientUserService.getUserByPhone(mobile);
                System.out.println("userByPhone1:"+newClient.toString());
                tokenService.createToken(newClient.getId());
                helperTokenEntity = tokenService.getByUserId(newClient.getId());


            }else{

                newClient = clientUserService.getUserByPhone(mobile);
                System.out.println("userByPhone1:"+newClient.toString());
                tokenService.createToken(newClient.getId());
                helperTokenEntity = tokenService.getByUserId(newClient.getId());

                map.put("helper_id",newClient.getId());
                map.put("helper_token",helperTokenEntity.getToken());
                map.put("helper_mobile",mobile);

                map.put("msg","仅新用户有效，不能重复助力！");
                result.setData(map);
                return result;
            }

        }

        if(newClient != null){
            map.put("helper_id",newClient.getId());
        }else{
            System.out.println("得到手机号："+mobile);
            ClientUserEntity userByMobile = clientUserDao.getUserByMobile(mobile);
            map.put("helper_id",userByMobile.getId());
        }

        map.put("helper_token",helperTokenEntity.getToken());

        map.put("helper_mobile",mobile);
        //------新用户创建完毕-----

        //3、检查活动
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, true);
        if(saItem == null){


            System.out.println("p0-活动是否存在或有效-saItem:"+saItem.toString());
            map.put("msg","感谢参与,活动已结束！");
            result.setData(map);
            return result;
        }
        //4、检查用户发起活动是不否存在
        SharingInitiatorEntity inProcessActivity = sharingInitiatorService.getOne(initiatorId, saId,ESharingInitiator.IN_PROCESSING.getCode());
        if(inProcessActivity == null){

            List<SharingInitiatorEntity> uActivitys = sharingInitiatorService.getList(initiatorId,saId,null);

            if(uActivitys.size()==0){
                result.setData(map);
                System.out.println("p1-用户未发起活动");
                map.put("msg","用户尚未发起本活动！");

                return result;
            }else{
                //默认排序，检查最后一个状态，是成功还是失败
                SharingInitiatorEntity sharingInitiatorEntity = uActivitys.get(uActivitys.size());
                if(sharingInitiatorEntity.getStatus() == ESharingInitiator.COMPLETE_SUCCESS.getCode()){
                    System.out.println("p2-活动已经完成");

                    result.setCode(200);
                    map.put("msg","本次活动已经完成，感谢参与！！");
                    result.setData(map);
                    return result;
                }else{
                    System.out.println("p3-活动超时失败");

                    map.put("msg","活动超时失败，请重新发起！！");
                    result.setData(map);
                    return result;
                }
            }
        }

//        if(!newUserOnly){
//            //4、检查用户助力次数
//
//        }
        //5、检查发起者完成的助力次数
        int allowHelpersNum = saItem.getHelpersNum();
        Integer completeCount = sharingActivityLogService.getCount(initiatorId, saId, inProcessActivity.getProposeId());


        //======================符合助力条件======================================

        //5、检查当前助力信息（是否最后一次）生成随机助力值（非最后一次）插入助力记录
        if((completeCount+1) < allowHelpersNum){
            //更新助力记录获得还需助力的费用值
//
            int rewardSum = sharingActivityLogService.getRewardSum(initiatorId,saId,inProcessActivity.getProposeId());

            if(rewardSum < inProcessActivity.getRewardValue()){
                System.out.println("198r还差的费用值："+(inProcessActivity.getRewardValue()-rewardSum)+","+inProcessActivity.getRewardValue());

                int itemReward = new  SharingActivityRandomUtil(inProcessActivity.getRewardValue()-rewardSum).getRandomValue();

                map.put("msg","成功为"+initiatorId+"助力："+itemReward+saItem.getRewardUnit()+"!");
                insertSharingActivityLog(saId,initiatorId,mobile,itemReward,inProcessActivity.getProposeId());
            }

        }else{

            //助力完成
            map.put("msg","助力成功"+initiatorId+"获得"+saItem.getRewardAmount()+saItem.getRewardUnit()+"!");

            //更新助力记录：获得还需助力的费用值
            int rewardSum = sharingActivityLogService.getRewardSum(initiatorId,saId,inProcessActivity.getProposeId());
            if(rewardSum<completeCount){
                int itemReward = saItem.getRewardAmount()-rewardSum;
                insertSharingActivityLog(saId,initiatorId,mobile,itemReward,inProcessActivity.getProposeId());
            }
            //更新发起者助力状态
            inProcessActivity.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());
            sharingInitiatorService.updateById(inProcessActivity);//重新更新表格状态

            prizes(saItem,initiatorId);//给用户增加代付金(目前仅有代付金)
        }
        ClientUserEntity byMobile = clientUserService.getByMobile(mobile);

        System.out.println("006");
        //6、更新被助力用户助力活动信息及token

        result.setCode(200);
        result.setData(map);

        return result;
    }
//=============================================================================================
    /**
     * `activity_id` int(10) NOT NULL COMMENT '活动编号',
     *   `initiator_id` bigint(20) unsigned NOT NULL COMMENT 'ct_client_user/id',
     *   `reward_amount` int(10) NOT NULL COMMENT '单项助力奖励',
     *   `helper_mobile` char(11) NOT NULL COMMENT '助力者手机号',
     *   `helper_value` int(10) NOT NULL COMMENT '助力值',
     *   `create_pmt` datetime DEFAULT NULL COMMENT '助力时间',
     */

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
    //更新用户奖励值
    private void prizes(SharingActivityEntity sharingActivityEntity,Long initiatorId){
        if(sharingActivityEntity.getRewardType()== 1){
            Integer rewardAmount = sharingActivityEntity.getRewardAmount();

            if(rewardAmount >= 100){
                String tmp = rewardAmount+"";
                String res = tmp.substring(0,tmp.length()-2)+"."+tmp.substring(1);
                clientUserService.addRecordGiftByUserid(initiatorId+"",res);
            }

        }
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
//            System.out.println("list.size:"+list.size());
//            for(int i= 0;i<list.size();i++){
//                System.out.println("list:"+list.get(i).toString());
//                int count = sharingInitiatorService.getCount(idOfClientUser,list.get(i).getSaId(),ESharingInitiator.IN_PROCESSING.getCode());
//                System.out.println("count:"+count);
//                if(list.get(i).getProposeTimes()==count){
//
//                    System.out.println("xxx");
//                    list.remove(i);
//                }else{
//                    System.out.println("yyy");
//                }
//
//            }
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

}
