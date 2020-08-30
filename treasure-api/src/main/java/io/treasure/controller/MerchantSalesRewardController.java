package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.service.MerchantSalesRewardService;
import io.treasure.service.MerchantWithdrawService;
import io.treasure.service.UserWithdrawService;
import io.treasure.task.item.WithdrawCommissionForMerchant;
import io.treasure.utils.AdressIPUtil;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.MchRewardUpdateQuery;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;
import io.treasure.vo.SalesRewardApplyForWithdrawVo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/mch_reward")
@Api(tags="商户销售奖励")
public class MerchantSalesRewardController {

    @Autowired
    private MerchantSalesRewardService merchantSalesRewardService;

    @Autowired(required = false)
    private MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;

    @Autowired(required = false)
    private MerchantWithdrawDao merchantWithdrawDao;

    @Autowired
    private MerchantWithdrawService merchantWithdrawService;

    @Autowired
    private SMSConfig smsConfig;

    @Autowired(required = false)
    private MerchantDao merchantDao;
    @Autowired
    private UserWithdrawService userWithdrawService;

    @Autowired
    private WithdrawCommissionForMerchant withdrawCommissionForMerchant;
    @Autowired
    private SendSMSUtil sendSMSUtil;

    @CrossOrigin
    @Login
    @GetMapping("params")
    @ApiOperation("销售奖励参数")
    public Result getParams(){
        MerchantSalesRewardEntity params = merchantSalesRewardService.getParams();
        return new Result().ok(params);
    }

    @CrossOrigin
    @Login
    @PutMapping("params")
    @ApiOperation("修改记录")
    public Result setParams(MerchantSalesRewardEntity entity){
        merchantSalesRewardService.setParams(entity);
        return new Result().ok("设置完成");
    }

    @CrossOrigin
    @Login
    @PostMapping("params")
    @ApiOperation("创建参数(仅初始时使用)")
    public Result insertOne(@RequestBody MerchantSalesRewardEntity entity) {
        merchantSalesRewardService.insertOne(entity);
        return new Result().ok("插入完成");
    }
    //===============================================第二部分记录CRUD===========================================

    @CrossOrigin
    @Login
    @GetMapping("my_reward_logs")
    @ApiOperation(value = "查询奖励记录",tags = "用于销售奖励,为商户返现")
    @ApiImplicitParam(name ="mId", value = "商户id", paramType = "query",required = false, dataType="long")

    public Result getRecords(Long mId) throws ParseException {

        QueryWrapper<MerchantSalesRewardRecordEntity>  queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("m_id",mId);
        List<MerchantSalesRewardRecordEntity> merchantSalesRewardRecordEntities = merchantSalesRewardRecordDao.selectList(queryWrapper);
        return new Result().ok(merchantSalesRewardRecordEntities);
    }


    @CrossOrigin
    @Login
    @GetMapping("reward_log")
    @ApiOperation("奖励记录(单条)")
    @ApiImplicitParam(name = "id", value = "记录编号", paramType = "query", required = true, dataType="Long")

    public Result getRecord(@RequestParam Long id) throws ParseException {

        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectById(id);
        return new Result().ok(entity);
    }
    @CrossOrigin
    @Login
    @GetMapping("reward_logs")
    @ApiOperation(value = "查询奖励记录",tags = "用于销售奖励,为商户返现")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="mId", value = "商户id", paramType = "query",required = false, dataType="long") ,
            @ApiImplicitParam(name = "rewardValue",value = "奖励值",required = false, paramType = "query", dataType="int"),
            @ApiImplicitParam(name = "outline",value="摘要",paramType = "query",required = false,dataType = "int"),
            @ApiImplicitParam(name = "minValue", value = "最小值", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "index",value="页码",paramType = "query",required = false,dataType = "int"),
            @ApiImplicitParam(name = "pagesNum",value="页数",paramType = "query",required = false,dataType = "int")
    })

    public Result<IPage<MerchantSalesRewardRecordEntity>> getRecords(Long mId,Integer rewardValue,
                                                                     String outline,Integer minValue,
                                                                     Integer index,Integer pagesNum) throws ParseException {

        MerchantSalesRewardRecordVo vo = new MerchantSalesRewardRecordVo();
        MerchantSalesRewardRecordEntity entity = new MerchantSalesRewardRecordEntity();

        System.out.println(mId);
        if(mId != null)
            entity.setMId(mId);
//        if(rewardValue != null)
//            entity.setRewardValue(rewardValue);
//        if(outline != null)
//            entity.setOutline(outline);
        if(minValue != null)
            vo.setMinValue(minValue.doubleValue());
        vo.setMerchantSalesRewardRecordEntity(entity);
        Integer indexs = index == null?0:index;
        Integer pagesNums = pagesNum==null?10:pagesNum;
        if(indexs >0)
            indexs --;
        vo.setIndex(indexs);
        vo.setPagesNum(pagesNums);

        IPage<MerchantSalesRewardRecordEntity> records = merchantSalesRewardService.getRecords(vo);
        return new Result().ok(records);
    }

    @CrossOrigin
    @Login
    @GetMapping("reward_cash")
    @ApiOperation("查询待提总现金额")
    @ApiImplicitParam(name = "id", value = "记录编号", paramType = "query", required = true, dataType="Long")

    public Result getRewardCash(@RequestParam Long id){

        String notWithdrawRewardAmount = merchantSalesRewardService.getNotWithdrawRewardAmount(id);
        return new Result().ok(notWithdrawRewardAmount);
    }

    @CrossOrigin
    @Login
    @DeleteMapping("reward_log")
    @ApiOperation("逻辑删除返现记录")
    public Result delRecord(@RequestBody Long id){
        merchantSalesRewardService.delRecord(id);
        return new Result().ok("删除成功");
    }

    @CrossOrigin
    @Login
    @PostMapping("reward_log")
    @ApiOperation("增加商户返现记录(单条)")
    public Result insertRecord(@RequestBody  MerchantSalesRewardRecordEntity entity){
        //增加校验
        Long mId = entity.getMId();
        String amount = merchantSalesRewardService.getNotWithdrawRewardAmount(mId);
//        if(amount.equals(entity.getRewardValue().toString())){
//            merchantSalesRewardService.insertRecord(entity);
//            return new Result().ok("添加记录成功");
//        }
        return new Result().ok("添加金额与实际金额不符");
    }

    @CrossOrigin
    @Login
    @PostMapping("reward_logs")
    @ApiOperation("增加商户返现记录(增加多条)")
    public Result insertRecords(@RequestBody  List<Long> mchIds){
        //增加校验
        List<RewardMchList> rewardMchList = merchantSalesRewardService.getRewardMchList(mchIds);

        merchantSalesRewardService.insertBatchRecords(rewardMchList);
        return new Result().ok("添加记录成功");
    }

    @CrossOrigin
    @Login
    @GetMapping("mch_list")
    @ApiOperation("取得需要奖励的准商家列表(全返)")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "ranking", value = "名次", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "minValue", value = "最小值", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "index",value="页码",paramType = "query",required = false,dataType = "int"),
            @ApiImplicitParam(name = "pagesNum",value="页数",paramType = "query",required = false,dataType = "int")
    })
    public Result getRewardMchList(Integer ranking,Integer minValue,Integer index,Integer pagesNum) throws ParseException {
        //!!!!!!!!!!!!!!!!!!!
        Date monthStop =  TimeUtil.getMonthEnd(null);

        MerchantSalesRewardRecordVo vo = new MerchantSalesRewardRecordVo();
        if(ranking != null){
            vo.setRanking(ranking);
        }
        if(minValue != null)
            vo.setMinValue(minValue.doubleValue());
        vo.setStopTime(monthStop);

        Integer indexs = index == null?0:index;
        Integer pagesNums = pagesNum==null?10:pagesNum;
        if(indexs >0)
            indexs --;
        indexs = indexs * pagesNums;
        System.out.println("index,pagesNums:"+index+","+pagesNums);

        vo.setIndex(indexs);
        vo.setPagesNum(pagesNums);


        List<RewardMchList> rewardMchList = merchantSalesRewardService.getRewardMchList(vo);
        return new Result().ok(rewardMchList);
    }
    //=========================================================================================

    @Login
    @PostMapping("apply_for")//商户申请，即商户id必须为一个
    @ApiOperation("申请提现")
    public Result save(@RequestBody SalesRewardApplyForWithdrawVo vo, @RequestParam HttpServletRequest request){

        List<MerchantSalesRewardRecordEntity> entities = vo.getEntities();
        if(entities.size()==0)
            return new Result().ok("no content");

        MchRewardUpdateQuery mchRewardUpdateQuery = new MchRewardUpdateQuery();
        List<Long> ids = new ArrayList<>();
        BigDecimal applyForValue = new BigDecimal("0");
        Long mId = null;
        for(int i=0;i<entities.size();i++){
            MerchantSalesRewardRecordEntity entity = entities.get(i);
            Long id = entity.getId();
            ids.add(id);

            if(mId == null)
                mId = entity.getMId();

            BigDecimal commissionVolume = entity.getCommissionVolume();
            applyForValue = applyForValue.add(commissionVolume);

            //修改提现方式
            entity.setMethod(vo.getWithDrawType());
            merchantSalesRewardRecordDao.updateById(entity);
        }
        if(mId != null){
            //准备微信支付ip地址
            MerchantEntity merchantEntity = merchantDao.selectById(mId);
            String ipAddress= AdressIPUtil.getClientIpAddress(request);
            if(ipAddress != null) {
                merchantEntity.setAddress(ipAddress);
            }
            merchantEntity.setCommissionAudit(merchantEntity.getCommissionAudit().subtract(applyForValue));
            merchantDao.updateById(merchantEntity);
        }
        mchRewardUpdateQuery.setIds(ids);
        mchRewardUpdateQuery.setStatus(3);//申请提现
        mchRewardUpdateQuery.setComment("商申请提现");
        merchantSalesRewardRecordDao.updateStatusByIds(mchRewardUpdateQuery);

        //给平台发送提现申请消息
        if(entities.size()== 1){
            MerchantSalesRewardRecordEntity entity = entities.get(0);
            Integer status = 0;//entity.getStatus();
            if(status == 3){
                MerchantEntity merchantEntity = merchantDao.selectById(entity.getMId());
                if(merchantEntity != null){
                    String fee = "0";
                    //BigDecimal value = new BigDecimal(entity.getRewardValue().toString());
                   // value = value.divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
                   // sendSMSUtil.commissionNotify("15303690053",merchantEntity.getName(),232+"", SendSMSUtil.CommissionNotifyType.SERVICE_NOTIFY);
                }
            }
        }else if(entities.size()> 1){
            sendSMSUtil.commissionNotify("15303690053","多位商家","X", SendSMSUtil.CommissionNotifyType.SERVICE_NOTIFY);
        }

        return new Result().ok("success");
    }

    @CrossOrigin
    @Login
    @PutMapping("refuse")
    @ApiOperation("拒绝提现")
    public Result refuse(@RequestBody List<MerchantSalesRewardRecordEntity> entities){

        if(entities.size()==0)
            return new Result().ok("no content");

        MchRewardUpdateQuery mchRewardUpdateQuery = new MchRewardUpdateQuery();
        List<Long> ids = new ArrayList<>();
        for(int i=0;i<entities.size();i++){
            Long id = entities.get(i).getId();
            ids.add(id);
        }
        mchRewardUpdateQuery.setIds(ids);
        mchRewardUpdateQuery.setStatus(2);
//        mchRewardUpdateQuery.setComment(entities.get(0).getAuditComment());
        merchantSalesRewardRecordDao.updateStatusByIds(mchRewardUpdateQuery);
        List<MerchantSalesRewardRecordEntity> smsEntities = merchantSalesRewardRecordDao.selectBatchIds(ids);
        for(int i=0;i<entities.size();i++){
            MerchantSalesRewardRecordEntity entity = smsEntities.get(i);
//            Integer status = entity.getStatus();
//            if(status == 2){
//                MerchantEntity merchantEntity = merchantDao.selectById(entity.getMId());
//                if(merchantEntity != null){
//                    String fee = "0";
//                    BigDecimal value = new BigDecimal(entity.getRewardValue().toString());
//                    value = value.divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
//                    sendSMSUtil.commissionNotify(merchantEntity.getMobile(),merchantEntity.getName(),value+"", SendSMSUtil.CommissionNotifyType.DENIED_NOTIFY);
//                }
//            }
        }
        return new Result().ok("refused");
    }

    @CrossOrigin
    @Login
    @PutMapping("agree")
    @ApiOperation("同意提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result agree(@RequestBody List<MerchantSalesRewardRecordEntity> entities){

        if(entities.size()==0)
            return new Result().ok("no content");

        MchRewardUpdateQuery mchRewardUpdateQuery = new MchRewardUpdateQuery();
        List<Long> ids = new ArrayList<>();
        for(int i=0;i<entities.size();i++){
            Long id = entities.get(i).getId();
            ids.add(id);
        }
        mchRewardUpdateQuery.setIds(ids);
        mchRewardUpdateQuery.setStatus(1);
//        mchRewardUpdateQuery.setComment(entities.get(0).getAuditComment());
        merchantSalesRewardRecordDao.updateStatusByIds(mchRewardUpdateQuery);

        //执行提现操作
        withdrawCommissionForMerchant.setForceRunOnce(true);
        withdrawCommissionForMerchant.resetTaskCounter();

        return new Result().ok("agreed");
    }
    public Result rewardCash(@RequestParam Long id,Integer method) throws ParseException {

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(reward_value) as reward_value");
        queryWrapper.eq("m_id",id);
        queryWrapper.eq("cash_out_status",1);
        queryWrapper.eq("status",1);

        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectOne(queryWrapper);
//        Integer canWithDraw = entity.getRewardValue();//可提现金额

        boolean withDrawResult = false;

        return new Result().ok(entity);//执行提现操作
    }


}
