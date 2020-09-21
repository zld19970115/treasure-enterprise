package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import io.treasure.annotation.Login;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantDao;
import io.treasure.dao.MerchantSalesRewardDao;
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
import io.treasure.vo.*;
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
    private MerchantSalesRewardDao merchantSalesRewardDao;

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
    public Result save(@RequestBody SalesRewardApplyForWithdrawVo vo, HttpServletRequest request){

        Long mId = vo.getMId();
        Integer withDrawType = vo.getWithDrawType();

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("m_id",mId);
        queryWrapper.eq("cash_out_status",1);
        queryWrapper.eq("audit_status",0);
        BigDecimal applyForValue = new BigDecimal("0");
        List<MerchantSalesRewardRecordEntity> entities = merchantSalesRewardRecordDao.selectList(queryWrapper);

        if(entities.size()==0){
            return new Result().ok("无可提现返佣！！");
        }
        List<Long> ids = new ArrayList<>();
        for(int i=0;i<entities.size();i++){
            ids.add(entities.get(i).getId());
            applyForValue = applyForValue.add(entities.get(i).getCommissionVolume());
        }
        MchRewardUpdateQuery query = new MchRewardUpdateQuery();
        query.setIds(ids);
        query.setStatus(3);
        query.setMethod(withDrawType);//1微信，2支付宝
        query.setComment("申请提佣");
        merchantSalesRewardRecordDao.updateAuditStatusByIds(query);

        //更新ip地址准备微信支付ip地址
        MerchantEntity merchantEntity = merchantDao.selectById(mId);
        String ipAddress= AdressIPUtil.getClientIpAddress(request);
        System.out.println("ipAddress:"+ipAddress);
        if(ipAddress != null) {
            merchantEntity.setAddress(ipAddress);
        }
        merchantEntity.setCommissionAudit(merchantEntity.getCommissionAudit().subtract(applyForValue));
        merchantDao.updateById(merchantEntity);

        String mchInfo = mId+"";
        mchInfo = "商家尾号"+mchInfo.substring(mchInfo.length()-6);
        MerchantEntity mchEntity = merchantDao.selectById(mId);
        if(mchEntity != null)
            mchInfo = mchEntity.getName();

        sendSMSUtil.commissionNotify("15303690053",mchInfo,":多个", SendSMSUtil.CommissionNotifyType.SERVICE_NOTIFY);
        return new Result().ok("success");
    }

    //====================总后台========================================================
    @CrossOrigin
    @Login
    @GetMapping("require_list")
    @ApiOperation("查询申请提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="auditStatus",value = "1同意2拒绝3请求",dataType = "int",defaultValue = "3",paramType = "query",required = false),
            @ApiImplicitParam(name="cashOutStatus",value = "1未提，2已提",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="method",value = "提现方式1微信，2支付宝",dataType = "int",paramType = "query",required = false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })

    public Result getRequestWithDrawList(Integer auditStatus,Integer cashOutStatus,Integer method,Integer index, Integer itemNum){

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("audit_status",auditStatus);
        queryWrapper.eq("cash_out_status",cashOutStatus);
        if(method != null)
            queryWrapper.eq("method",method);

        Page<MerchantSalesRewardRecordEntity> map = new Page<MerchantSalesRewardRecordEntity>(index,itemNum);
        IPage<MerchantSalesRewardRecordEntity> pages = merchantSalesRewardRecordDao.selectPage(map, queryWrapper);
        for(MerchantSalesRewardRecordEntity obj : pages.getRecords()) {
            obj.setMerName(merchantDao.selectById(obj.getMId()).getName());
        }
        return new Result().ok(pages);
    }


    @CrossOrigin
    @Login
    @GetMapping("refuse")
    @ApiOperation("拒绝提现-只包括id就可以")
    public Result refuse(@RequestParam  Long rid){
        MerchantSalesRewardRecordEntity obj = new MerchantSalesRewardRecordEntity();
        obj.setId(rid);
        List<MerchantSalesRewardRecordEntity> entities = Lists.newArrayList();
        entities.add(obj);

        MchRewardUpdateQuery mchRewardUpdateQuery = new MchRewardUpdateQuery();
        List<Long> ids = new ArrayList<>();
        for(int i=0;i<entities.size();i++){
            Long id = entities.get(i).getId();
            ids.add(id);
        }
        mchRewardUpdateQuery.setIds(ids);
        mchRewardUpdateQuery.setStatus(2);
        mchRewardUpdateQuery.setComment(entities.get(0).getAuditComment());
        merchantSalesRewardRecordDao.updateStatusByIds(mchRewardUpdateQuery);

        List<MerchantSalesRewardRecordEntity> smsEntities = merchantSalesRewardRecordDao.selectBatchIds(ids);
        if(smsEntities.size() > 0) {
            for(int i=0;i<entities.size();i++){
                MerchantSalesRewardRecordEntity entity = smsEntities.get(i);
                Integer auditStatus = entity.getAuditStatus();
                if(auditStatus == 2){
                    MerchantEntity merchantEntity = merchantDao.selectById(entity.getMId());
                    if(merchantEntity != null){
                        BigDecimal value = new BigDecimal(entity.getCommissionVolume().toString());
                        if(merchantEntity.getMobile() != null){
                            sendSMSUtil.commissionNotify(merchantEntity.getMobile(),merchantEntity.getName(),value+"", SendSMSUtil.CommissionNotifyType.DENIED_NOTIFY);
                        }
                    }
                }
            }
        }
        return new Result().ok("refused");
    }

    @CrossOrigin
    @Login
    @PostMapping("refuse")
    @ApiOperation("拒绝提现-只包括id就可以")
    public Result refuseX(@RequestBody  Long rid){
        MerchantSalesRewardRecordEntity obj = new MerchantSalesRewardRecordEntity();
        obj.setId(rid);
        List<MerchantSalesRewardRecordEntity> entities = Lists.newArrayList();
        entities.add(obj);

        MchRewardUpdateQuery mchRewardUpdateQuery = new MchRewardUpdateQuery();
        List<Long> ids = new ArrayList<>();
        for(int i=0;i<entities.size();i++){
            Long id = entities.get(i).getId();
            ids.add(id);
        }
        mchRewardUpdateQuery.setIds(ids);
        mchRewardUpdateQuery.setStatus(2);
        mchRewardUpdateQuery.setComment(entities.get(0).getAuditComment());
        merchantSalesRewardRecordDao.updateStatusByIds(mchRewardUpdateQuery);

        List<MerchantSalesRewardRecordEntity> smsEntities = merchantSalesRewardRecordDao.selectBatchIds(ids);
        if(smsEntities.size() > 0) {
            for(int i=0;i<entities.size();i++){
                MerchantSalesRewardRecordEntity entity = smsEntities.get(i);
                Integer auditStatus = entity.getAuditStatus();
                if(auditStatus == 2){
                    MerchantEntity merchantEntity = merchantDao.selectById(entity.getMId());
                    if(merchantEntity != null){
                        BigDecimal value = new BigDecimal(entity.getCommissionVolume().toString());
                        if(merchantEntity.getMobile() != null){
                            sendSMSUtil.commissionNotify(merchantEntity.getMobile(),merchantEntity.getName(),value+"", SendSMSUtil.CommissionNotifyType.DENIED_NOTIFY);
                        }
                    }
                }
            }
        }
        return new Result().ok("refused");
    }

    @CrossOrigin
    @Login
    @PostMapping("agree")
    @ApiOperation("同意提现-只包含id就可以")
    public Result agree(@RequestBody Long mId){

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("m_id",mId);
        queryWrapper.eq("cash_out_status",1);
        queryWrapper.eq("audit_status",3);
        BigDecimal applyForValue = new BigDecimal("0");
        List<MerchantSalesRewardRecordEntity> entities = merchantSalesRewardRecordDao.selectList(queryWrapper);

        if(entities.size()==0){
            return new Result().ok("没有找到返佣提现请求！！");
        }
        List<Long> ids = new ArrayList<>();
        for(int i=0;i<entities.size();i++){
            ids.add(entities.get(i).getId());
            applyForValue = applyForValue.add(entities.get(i).getCommissionVolume());
        }
        MchRewardUpdateQuery query = new MchRewardUpdateQuery();
        query.setIds(ids);
        query.setStatus(1);//同意
        query.setMethod(null);//1微信，2支付宝
        query.setComment("同意提现");
        merchantSalesRewardRecordDao.updateAuditStatusByIds(query);

        //更新商户反佣相关费用值
        MerchantEntity merchantEntity = merchantDao.selectById(mId);
        merchantEntity.setCommissionAudit(merchantEntity.getCommissionAudit().subtract(applyForValue));
        merchantDao.updateById(merchantEntity);

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


    @CrossOrigin
    @Login
    @GetMapping("params")
    @ApiOperation("销售奖励参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "参数id",dataType = "Long",paramType = "query",required = false),
            @ApiImplicitParam(name="mId",value = "商家id",dataType = "Long",paramType = "query",required = false)
    })
    public Result getParams(Long id,Long mId){
        if(id != null){
            MerchantSalesRewardEntity merchantSalesRewardEntity = merchantSalesRewardDao.selectById(id);
            return new Result().ok(merchantSalesRewardEntity);
        }else{
            if(mId != null){
                //根据类型，单个
                MerchantEntity merchantEntity = merchantDao.selectById(mId);
                if(merchantEntity != null){
                    Integer commissionType = merchantEntity.getCommissionType();
                    if(commissionType != null){
                        QueryWrapper<MerchantSalesRewardEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("merchant_type",commissionType);
                        List<MerchantSalesRewardEntity> merchantSalesRewardEntities = merchantSalesRewardDao.selectList(queryWrapper);
                        return new Result().ok(merchantSalesRewardEntities);
                    }else{
                        return new Result().ok(null);
                    }
                }else{
                    return new Result().ok(null);
                }
            }else{
                //列表
                List<MerchantSalesRewardEntity> merchantSalesRewardEntities = merchantSalesRewardDao.selectList(null);
                return new Result().ok(merchantSalesRewardEntities);
            }

        }
    }

    @CrossOrigin
    @Login
    @PostMapping("params")
    @ApiOperation("插入新记录")
    public Result insertParams(@RequestBody MerchantSalesRewardEntity entity){
        QueryWrapper<MerchantSalesRewardEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_type",entity.getMerchantType());
        List<MerchantSalesRewardEntity> merchantSalesRewardEntities = merchantSalesRewardDao.selectList(queryWrapper);
        if(merchantSalesRewardEntities.size() > 0)
            return  new Result().ok("exist already!");
        try{
            entity.setId(null);
            merchantSalesRewardDao.insert(entity);
        }catch (Exception e){
            return new Result().ok("exception");
        }
        return new Result().ok("success");
    }
    @CrossOrigin
    @Login
    @PutMapping("params")
    @ApiOperation("修改记录")
    public Result setParams(@RequestBody MerchantSalesRewardEntity entity){
        if(entity.getId() == null)
            return new Result().ok("failure:id is null");
        try{
            merchantSalesRewardDao.updateById(entity);
        }catch (Exception e){
            return new Result().ok("exception");
        }
        return new Result().ok("success");
    }

}
