package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dao.SharingActivityDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.PowerContentDTO;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.*;
import io.treasure.service.*;
import io.treasure.utils.SharingActivityRandomUtil;
import oracle.jdbc.proxy.annotation.Post;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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


    @Autowired
    private SharingActivityService sharingActivityService;

    @Autowired
    private SharingInitiatorService sharingInitiatorService;

    @Autowired
    private SharingActivityLogService sharingActivityLogService;

    @Autowired(required = false)
    private SharingActivityLogDao sharingActivityLogDao;


    /**
     * @param dto 协助助力的用户信息
     * @param initiator_id    发起助力者的client_id
     * @param sa_id  活动编号
     * @return
     */
    @PostMapping("as_helper")
    @ApiOperation("注册并帮好友助力")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "initiator_id", value = "发起者Id",required = true, paramType = "query", dataType="long"),
            @ApiImplicitParam(name = "sa_id", value = "助力活动编号",required = true, paramType = "query", dataType="int")
    })
    public Result helpRelay(@RequestBody ClientUserDTO dto, Long initiator_id,Integer sa_id) {
        System.out.println("id,id"+initiator_id+","+sa_id);
        Result result = new Result();
        result.setCode(501);
        //1、表单校验
        ValidatorUtils.validateEntity(dto);

        //检查是否有token如果没有则插入
        if(clientUserService.getUserByPhone(dto.getMobile()) == null){
            ClientUserEntity user = new ClientUserEntity();
            user.setMobile(dto.getMobile());
            user.setUsername(dto.getMobile());
            user.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
            user.setCreateDate(new Date());
            user.setUnionid(dto.getUnionid());
            user.setClientId(dto.getClientId());
            clientUserService.insert(user);
        }
        ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(dto.getMobile());

        tokenService.createToken(userByPhone1.getId());
        TokenEntity tokenEntity = tokenService.getByUserId(userByPhone1.getId());

        //3、发起的检查助力活动状态，不存在返回活动已失效
        SharingActivityEntity saItem = sharingActivityService.getOneById(sa_id, true);
        if(saItem == null){
            result.setMsg("感谢参与,活动已结束！");
            return result;
        }
        int siCount = sharingInitiatorService.getCount(initiator_id, sa_id,ESharingInitiator.IN_PROCESSING.getCode());
        if(siCount == 0){
            result.setMsg("感谢参与,活动已结束！");
            return result;
        }
        //4、检查用户是否助力过，助力过则返回不可重复助力
        int helpedCountMyself = sharingActivityLogService.getHelpedCount(initiator_id, sa_id, dto.getMobile());

        if(saItem.getRepeatableTimes()<=helpedCountMyself){
            result.setMsg("感谢参与,助力次数达限！");
            return result;
        }
        int helpedCount = sharingActivityLogService.getCount(initiator_id,sa_id);
        if(saItem.getHelpersNum() < (helpedCount+1)){//当前已助力次数

            //防止并发锁问题（如果助力状态未改，则修改状态并显示助力成功，否则为手慢了助力已成功）
            SharingInitiatorEntity oneByInitiatorId = sharingInitiatorService.getOneByInitiatorId(initiator_id, sa_id);
            if(oneByInitiatorId.getStatus() == ESharingInitiator.IN_PROCESSING.getCode()){
                oneByInitiatorId.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());
                sharingInitiatorService.updateOneByInitiatorId(oneByInitiatorId);//重新更新表格状态

                execReward(saItem,initiator_id);//给用户增加代付金(目前仅有代付金)
            }
            result.setMsg("感谢参与,助力次数达限！");
            return result;
        }
        //======================符合助力条件======================================
        Map map = new HashMap();
        //5、检查当前助力信息（是否最后一次）生成随机助力值（非最后一次）插入助力记录
        if(saItem.getHelpersNum()== (helpedCount+1)){
            //最后一次
            map.put("msg","助力成功"+initiator_id+"获得"+saItem.getRewardAmount()+saItem.getRewardUnit()+"!");

            //更新助力记录：获得还需助力的费用值
            int sumReward = sharingActivityLogService.getSum(initiator_id,sa_id);
            if(saItem.getRewardAmount()>sumReward){
                int itemReward = saItem.getRewardAmount()-sumReward;
                updateSALog(sa_id,initiator_id,dto.getMobile(),itemReward);
            }

            SharingInitiatorEntity oneByInitiatorId = sharingInitiatorService.getOneByInitiatorId(initiator_id, sa_id);
            if(oneByInitiatorId.getStatus() == ESharingInitiator.IN_PROCESSING.getCode()){
                oneByInitiatorId.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());
                sharingInitiatorService.updateOneByInitiatorId(oneByInitiatorId);//重新更新表格状态

                execReward(saItem,initiator_id);//给用户增加代付金(目前仅有代付金)
            }


        }else{
            //更新助力记录获得还需助力的费用值
            int sumReward = sharingActivityLogService.getSum(initiator_id,sa_id);
            if(saItem.getRewardAmount()>sumReward){
                int itemReward = new  SharingActivityRandomUtil(saItem.getRewardAmount()-sumReward).getRandomValue();
                updateSALog(sa_id,initiator_id,dto.getMobile(),itemReward);
                map.put("msg","成功为"+initiator_id+"助力："+itemReward+saItem.getRewardUnit()+"!");

            }
        }

        //6、更新被助力用户助力活动信息及token
        map.put("helper_id",dto.getClientId());
        map.put("helper_mobile",dto.getMobile());

        map.put("helper_token",tokenEntity.getToken());
        result.setCode(200);
        result.setData(map);

        return result;
    }

    /**
     * 协助助力的用户信息(参数方式)
     * @param initiator_id    发起助力者的client_id
     * @param sa_id  活动编号
     * @return
     */
    @PostMapping("p_helper")
    @ApiOperation("注册并帮好友助力1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "initiator_id", value = "发起者Id",required = true, paramType = "query", dataType="long"),
            @ApiImplicitParam(name = "sa_id", value = "助力活动编号",required = true, paramType = "query", dataType="int"),

            @ApiImplicitParam(name = "mobile", value = "手机号",required = true, paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "password", value = "密码",required = true, paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "unionid", value = "union编号",required = true, paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "clientId", value = "client编号",required = true, paramType = "query", dataType="String")
    })
    public Result helpRelayParam(Long initiator_id,Integer sa_id,String mobile,String password,String unionid,String clientId) {
        System.out.println("id,id"+initiator_id+","+sa_id);
        Result result = new Result();
        result.setCode(501);

        //检查是否有token如果没有则插入
        if(clientUserService.getUserByPhone(mobile) == null){
            ClientUserEntity user = new ClientUserEntity();
            user.setMobile(mobile);
            user.setUsername(mobile);
            user.setPassword(DigestUtils.sha256Hex(password));
            user.setCreateDate(new Date());
            user.setUnionid(unionid);
            user.setClientId(clientId);
            clientUserService.insert(user);
        }
        ClientUserEntity userByPhone1 = clientUserService.getUserByPhone(mobile);

        tokenService.createToken(userByPhone1.getId());
        TokenEntity tokenEntity = tokenService.getByUserId(userByPhone1.getId());

        //3、发起的检查助力活动状态，不存在返回活动已失效
        SharingActivityEntity saItem = sharingActivityService.getOneById(sa_id, true);
        if(saItem == null){
            result.setMsg("感谢参与,活动已结束(0)！");
            return result;
        }
        int siCount = sharingInitiatorService.getCount(initiator_id, sa_id,1);
        System.out.println("status"+siCount);
        if(siCount == 0){
            result.setMsg("感谢参与,活动已结束(1)！");
            return result;
        }
        //4、检查用户是否助力过，助力过则返回不可重复助力
        int helpedCountMyself = sharingActivityLogService.getHelpedCount(initiator_id, sa_id,mobile);

        if(saItem.getRepeatableTimes()<=helpedCountMyself){
            result.setMsg("感谢参与,助力次数达限(0)！");
            return result;
        }
        int helpedCount = sharingActivityLogService.getCount(initiator_id,sa_id);
        if(saItem.getHelpersNum() < (helpedCount+1)){//当前已助力次数

            //防止并发锁问题（如果助力状态未改，则修改状态并显示助力成功，否则为手慢了助力已成功）
            SharingInitiatorEntity oneByInitiatorId = sharingInitiatorService.getOneByInitiatorId(initiator_id, sa_id);
            if(oneByInitiatorId.getStatus() == ESharingInitiator.IN_PROCESSING.getCode()){
                oneByInitiatorId.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());
                sharingInitiatorService.updateOneByInitiatorId(oneByInitiatorId);//重新更新表格状态

                execReward(saItem,initiator_id);//给用户增加代付金(目前仅有代付金)
            }
            result.setMsg("感谢参与,助力次数达限(1)！");
            return result;
        }
        //======================符合助力条件======================================
        Map map = new HashMap();
        //5、检查当前助力信息（是否最后一次）生成随机助力值（非最后一次）插入助力记录
        if(saItem.getHelpersNum()== (helpedCount+1)){
            //最后一次
            map.put("msg","助力成功"+initiator_id+"获得"+saItem.getRewardAmount()+saItem.getRewardUnit()+"!");

            //更新助力记录：获得还需助力的费用值
            int sumReward = sharingActivityLogService.getSum(initiator_id,sa_id);
            if(saItem.getRewardAmount()>sumReward){
                int itemReward = saItem.getRewardAmount()-sumReward;
                updateSALog(sa_id,initiator_id,mobile,itemReward);
            }

            SharingInitiatorEntity oneByInitiatorId = sharingInitiatorService.getOneByInitiatorId(initiator_id, sa_id);
            if(oneByInitiatorId.getStatus() == ESharingInitiator.IN_PROCESSING.getCode()){
                oneByInitiatorId.setStatus(ESharingInitiator.COMPLETE_SUCCESS.getCode());
                sharingInitiatorService.updateOneByInitiatorId(oneByInitiatorId);//重新更新表格状态

                execReward(saItem,initiator_id);//给用户增加代付金(目前仅有代付金)
            }


        }else{
            //更新助力记录获得还需助力的费用值
//
            int sumReward = sharingActivityLogService.getSum(initiator_id,sa_id);

            if(saItem.getRewardAmount()>sumReward){
                int itemReward = new  SharingActivityRandomUtil(saItem.getRewardAmount()-sumReward).getRandomValue();
                updateSALog(sa_id,initiator_id,mobile,itemReward);
                map.put("msg","成功为"+initiator_id+"助力："+itemReward+saItem.getRewardUnit()+"!");

            }
        }

        //6、更新被助力用户助力活动信息及token
        map.put("helper_id",clientId);
        map.put("helper_mobile",mobile);

        map.put("helper_token",tokenEntity.getToken());
        result.setCode(200);
        result.setData(map);

        return result;
    }

    /**
     * `activity_id` int(10) NOT NULL COMMENT '活动编号',
     *   `initiator_id` bigint(20) unsigned NOT NULL COMMENT 'ct_client_user/id',
     *   `reward_amount` int(10) NOT NULL COMMENT '单项助力奖励',
     *   `helper_mobile` char(11) NOT NULL COMMENT '助力者手机号',
     *   `helper_value` int(10) NOT NULL COMMENT '助力值',
     *   `create_pmt` datetime DEFAULT NULL COMMENT '助力时间',
     */

    //更新记录助力记录
    private void updateSALog(int activityId,long initaiatorId,String helperMobile,int helpValue){
        SharingActivityLogEntity sharingActivityLogEntity = new SharingActivityLogEntity();
        sharingActivityLogEntity.setActivityId(activityId);
        sharingActivityLogEntity.setInitiatorId(initaiatorId);
        sharingActivityLogEntity.setHelperMobile(helperMobile);
        sharingActivityLogEntity.setHelperValue(helpValue);
        sharingActivityLogService.insertOne(sharingActivityLogEntity);
    }
    //更新用户奖励值
    private void execReward(SharingActivityEntity sharingActivityEntity,Long initiatorId){
        if(sharingActivityEntity.getRewardType()== 1){
            Integer rewardAmount = sharingActivityEntity.getRewardAmount();

            if(rewardAmount >= 100){
                String tmp = rewardAmount+"";
                String res = tmp.substring(0,tmp.length()-2)+"."+tmp.substring(1);
                clientUserService.addRecordGiftByUserid(initiatorId+"",res);
            }

        }
    }

    @PostMapping("startRelay")
    @ApiOperation("发起助力")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "clientUserId",required = true, paramType = "query", dataType="long"),
            @ApiImplicitParam(name = "saId", value = "助力活动编号",required = true, paramType = "query", dataType="int"),
    })
    public Result startRelay(Long id,Integer saId){
        //1、检查活动是否存在
        SharingActivityEntity saItem = sharingActivityService.getOneById(saId, true);
        if(saItem == null)
            return new Result().error("活动已失效！");
        //2、检查用户是否发起过助力,发起过则返回用户已参加过活动不能重复参与此活动
        SharingInitiatorEntity oneByInitiatorId = sharingInitiatorService.getOneByInitiatorId(id, saId);
        if(oneByInitiatorId != null){

            //System.out.println("times,times:"+saItem.getProposeTimes()+","+sharingInitiatorService.getCount(id,saId,null));
            //System.out.println("count"+sharingInitiatorService.getCount(id,saId,ESharingInitiator.IN_PROCESSING.getCode()));
            if(saItem.getProposeTimes()>sharingInitiatorService.getCount(id,saId,null)
                    && sharingInitiatorService.getCount(id,saId,ESharingInitiator.IN_PROCESSING.getCode()) == 0){
                //活动次数未超限，没有进行中的同编号活动===>可以发起活动

                return initSharingActivityForClient(id,saId,saItem.getRewardType(),saItem.getRewardAmount(),saItem.getSubject());
            }
            return new Result().error("活动已失效！");
        }else{

        }
        //====>可以发起活动
        //3、返回用户的client_id和助力活动信息
        return initSharingActivityForClient(id,saId,saItem.getRewardType(),saItem.getRewardAmount(),saItem.getSubject());
    }

    private Result initSharingActivityForClient(Long id,Integer saId,Integer rewardType,Integer rewardValue,String saTitle){

        SharingInitiatorEntity siEntity = new SharingInitiatorEntity();
        siEntity.setInitiatorId(id);
        siEntity.setSaId(saId);
        siEntity.setStatus(ESharingInitiator.IN_PROCESSING.getCode());
        siEntity.setRewardType(rewardType);
        siEntity.setRewardValue(rewardValue);
        siEntity.setStartTime(new Date());
        sharingInitiatorService.insertOne(siEntity);

        Result result = new Result();
        result.setCode(200);
        result.setMsg("成功发起:\""+saTitle+"\"活动!!");
        return result;
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
