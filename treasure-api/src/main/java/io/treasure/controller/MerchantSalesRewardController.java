package io.treasure.controller;

import cn.jiguang.common.utils.TimeUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.dao.*;
import io.treasure.dto.GoodDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.service.MerchantSalesRewardService;
import io.treasure.service.MerchantWithdrawService;
import io.treasure.service.UserWithdrawService;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @CrossOrigin
    @Login
    @GetMapping("reward_logs")
    @ApiOperation(value = "奖励记录",tags = "用于销售奖励,为商户返现")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="mId", value = "商户id", paramType = "query",required = false, dataType="long") ,
            @ApiImplicitParam(name = "rewardValue",value = "奖励值",required = false, paramType = "query", dataType="int"),
            @ApiImplicitParam(name = "outline",value="摘要",paramType = "query",required = false,dataType = "int"),
            @ApiImplicitParam(name = "createPmt",value="创建时间",paramType = "query",required = false,dataType = "date"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = false, dataType="date") ,
            @ApiImplicitParam(name = "stopTime", value = "结束时间", paramType = "query",required = false, dataType="date") ,
            @ApiImplicitParam(name = "minValue", value = "最小值", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "index",value="页码",paramType = "query",required = false,dataType = "int"),
            @ApiImplicitParam(name = "pagesNum",value="页数",paramType = "query",required = false,dataType = "int")
    })

    public Result<IPage<MerchantSalesRewardRecordEntity>> getRecords(Long mId,Integer rewardValue,String outline,
                                                                     Date createPmt,Date startTime,Date stopTime,Integer minValue,
                                                                     Integer index,Integer pagesNum) throws ParseException {

        MerchantSalesRewardRecordVo vo = new MerchantSalesRewardRecordVo();

        MerchantSalesRewardRecordEntity entity = new MerchantSalesRewardRecordEntity();

        System.out.println(mId);
        if(mId != null)
            entity.setMId(mId);
        if(rewardValue != null)
            entity.setRewardValue(rewardValue);
        if(outline != null)
            entity.setOutline(outline);
        if(createPmt != null)
            entity.setCreatePmt(createPmt);
        if(startTime != null)
            vo.setStartTime(startTime);
        if(stopTime != null)
            vo.setStopTime(stopTime);
        if(minValue != null)
            vo.setMinValue(minValue.doubleValue());
        if(startTime == null && stopTime == null && createPmt == null){
            Date currentMonthStart = TimeUtil.getMonthStart(null);
            Date currentMonthStop = TimeUtil.getMonthEnd(null);
        }
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
    @GetMapping("reward_log")
    @ApiOperation("奖励记录(单条)")
    @ApiImplicitParam(name = "id", value = "记录编号", paramType = "query", required = true, dataType="Long")

    public Result getRecord(@RequestParam Long id) throws ParseException {

        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectById(id);
        return new Result().ok(entity);
    }


    @CrossOrigin
    @Login
    @GetMapping("reward_cash")
    @ApiOperation("查询待提总现金额")
    @ApiImplicitParam(name = "id", value = "记录编号", paramType = "query", required = true, dataType="Long")

    public Result getRewardCash(@RequestParam Long id) throws ParseException {

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(reward_value) as reward_value");
        queryWrapper.eq("m_id",id);
        queryWrapper.eq("cash_out_status",1);

        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectOne(queryWrapper);
        return new Result().ok(entity);
    }

    @Autowired(required = false)
    private MerchantDao merchantDao;
    @Autowired
    private UserWithdrawService userWithdrawService;

    @CrossOrigin
    @Login
    @PutMapping("reward_cash")
    @ApiOperation("商家佣金提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商户id", paramType = "query", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "method", value = "提现方式", paramType = "query",required = true, dataType="int")
    })
    public Result mchWithDraw(@RequestParam Long id,Integer method) throws ParseException {

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(reward_value) as reward_value");
        queryWrapper.eq("m_id",id);
        queryWrapper.eq("cash_out_status",1);

        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectOne(queryWrapper);
        Integer canWithDraw = entity.getRewardValue();//可提现金额


        boolean withDrawResult = false;
        if(method == 2){
            //userWithdrawService.AliMerchantCommissionWithDraw();
        }else{
            //userWithdrawService.wxMerchantCommissionWithDraw();
        }

        //执行提现操作


        return new Result().ok(entity);
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
        merchantSalesRewardService.insertRecord(entity);
        return new Result().ok("添加记录成功");
    }

    @CrossOrigin
    @Login
    @PostMapping("reward_logs")
    @ApiOperation("增加商户返现记录(自动增加)")
    public Result insertRecords(@RequestBody  List<RewardMchList> list){

        merchantSalesRewardService.insertBatchRecords(list);
        return new Result().ok("添加记录成功");
    }

    @CrossOrigin
    @Login
    @GetMapping("mch_list")
    @ApiOperation("取得需要奖励的商家列表(全返)")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "ranking", value = "名次", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "minValue", value = "最小值", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "yearMonth",value="指定时间",paramType = "query",required = false,dataType = "string"),
            @ApiImplicitParam(name = "index",value="页码",paramType = "query",required = false,dataType = "int"),
            @ApiImplicitParam(name = "pagesNum",value="页数",paramType = "query",required = false,dataType = "int")
    })
    public Result getRewardMchList(Integer ranking,Integer minValue,String yearMonth,Integer index,Integer pagesNum) throws ParseException {

        Date monthStart = null;
        Date monthStop = null;
        if(yearMonth != null){
            String[] split = yearMonth.split("-");
            String tTime = split[0]+"-"+split[1]+"-01 00:00:00";
            Date parse = TimeUtil.simpleDateFormat.parse(tTime);
            monthStart = TimeUtil.getMonthStart(parse);
            monthStop = TimeUtil.getMonthEnd(parse);
        }else{
            monthStart = TimeUtil.getMonthStart(null);
            monthStop = TimeUtil.getMonthEnd(null);
        }

        MerchantSalesRewardRecordVo vo = new MerchantSalesRewardRecordVo();
        if(ranking != null){
            vo.setRanking(ranking);
        }
        if(minValue != null)
            vo.setMinValue(minValue.doubleValue());
        vo.setStartTime(monthStart);
        vo.setStopTime(monthStop);

        Integer indexs = index == null?0:index;
        Integer pagesNums = pagesNum==null?10:pagesNum;
        if(indexs >0)
            indexs --;
        index = index * pagesNums;

        vo.setIndex(indexs);
        vo.setPagesNum(pagesNums);

        List<RewardMchList> rewardMchList = merchantSalesRewardService.getRewardMchList(vo);
        return new Result().ok(rewardMchList);
    }

}
