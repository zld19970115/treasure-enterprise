package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.*;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.impl.MerchantServiceImpl;
import io.treasure.service.impl.StatisticsServiceImpl;
import io.treasure.utils.DateUtil;
import io.treasure.utils.RegularUtil;
import io.treasure.vo.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商家统计表
 * 2019.8.26
 */
@RestController
@RequestMapping("/statistics")
@Api(tags="商家统计表")
public class StatisticsController {
    @Autowired
    private StatisticsServiceImpl statisticsService;
    @Autowired
    private MerchantServiceImpl merchantService;
    @GetMapping("/sta")
    @ApiOperation("统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "编号", paramType = "query", required = false, dataType="String"),
            @ApiImplicitParam(name = "startTime1", value = "开始日期", paramType = "query", required = false, dataType="String"),
            @ApiImplicitParam(name = "endTime1", value = "截止日期", paramType = "query", required = false, dataType="String")
    })
    public Result todayOrder(@ApiIgnore @RequestParam Map<String, Object> params) {
        String merchantId = (String) params.get("merchantId");

        Map map = new HashMap();
        //获取本日日期`111111q    `
        String format = DateUtil.getToday();

        //获取本月
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        params.put("format",format);
        params.put("month",month);
        //查询今日订单
        int todayOrder = statisticsService.todayOrder(params);
        //查询指定日期得全部订单
        int assignOrder = statisticsService.assignOrder(params);

        //查询今日预定订单
        int todayReserve = statisticsService.todayReserve( params);
        //查询指定日期得预定订单
        int assignReserve = statisticsService.assignReserve(params);

        //查询今日退订订单
        int todayQuit = statisticsService.todayQuit(params);
        //查询指定日期得预定订单
        int assignQuit = statisticsService.assignQuit(params);
        //查询今日实际收入
        double todayMoney = statisticsService.todayMoney(params);
        //查询指定日期得实际收入
        double assignMoney = statisticsService.assignMoney(params);
        //查询本月全部订单
        int monthOrder = statisticsService.monthOrder(params);
        //查询本月全部预定订单
        int monthReserve = statisticsService.monthReserve(params);
        //查询本月全部退订订单
        int monthQuit = statisticsService.monthQuit(params);
        //查询本月实际收入
        Double monthMoney = statisticsService.monthMoney(params);
        //查询全部实际收入
     //  Double allMoney = statisticsService.allMoney(merchantId);
        MerchantEntity merchantEntity = merchantService.selectById(merchantId);
        if (merchantEntity!=null){
            //查询商家可提现总额
            //map.put("total_cash",merchantEntity.getTotalCash());
            map.put("total_cash",statisticsService.getTotalCash(params).doubleValue());
            //查询商家已提现金额
            map.put("already_cash",merchantEntity.getAlreadyCash());
            //查询商家未提现金额
            map.put("not_cash",merchantEntity.getNotCash());

        }
        map.put("todayOrder",todayOrder); //查询今日订单
        map.put("todayReserve",todayReserve); //查询今日预定订单
        map.put("todayQuit",todayQuit); //查询今日退订订单
        map.put("todayMoney",todayMoney);  //查询今日实际收入

        map.put("assignOrder",assignOrder); //查询指定日期得全部订单
        map.put("assignReserve",assignReserve); //查询指定日期得预定订单
        map.put("assignQuit",assignQuit);  //查询指定日期得退订订单
        map.put("assignMoney",assignMoney);  //查询指定日期得实际收入

        map.put("monthOrder",monthOrder); //查询本月全部订单
        map.put("monthReserve",monthReserve);   //查询本月全部预定订单
        map.put("monthQuit",monthQuit); //查询本月全部退订订单
        map.put("monthMoney",monthMoney);   //查询本月实际收入

        map.put("completaOrder",statisticsService.getCompletaOrder(params).doubleValue());
        return new Result().ok(map);
    }

    @Login
    @PostMapping("getTopSellersRanking")
    @ApiOperation("查询商户热销产品排行统计")
    public Result<List<TopSellersRankingVo>> getTopSellersRanking(@RequestBody TopSellersRankingDto dto) {
        if(dto == null || dto.getMerchantId() == null) {
            return new Result().error("商户ID不能为空!");
        }
        if(Strings.isNotBlank(dto.getStartDate()) && !RegularUtil.dateSlashRegular(dto.getStartDate())) {
            return new Result().error("开始日期错误(格式:yyyy-mm-dd)!");
        }
        if(Strings.isNotBlank(dto.getEndDate()) && !RegularUtil.dateSlashRegular(dto.getEndDate())) {
            return new Result().error("结束日期错误(格式:yyyy-mm-dd)!");
        }
        if(dto.getSortField() != null && !Arrays.asList(new Integer[] {0,1}).contains(dto.getSortField())) {
            return new Result().error("排序字段参数不正确!");
        }
        if(dto.getSort() != null && dto.getSort() != 0 && dto.getSort() != 1) {
            return new Result().error("排序参数不正确!");
        }
        return new Result().ok(statisticsService.getTopSellersRanking(dto));
    }

    @Login
    @PostMapping("getConsumptionRanking")
    @ApiOperation("查询商户热销产品排行统计")
    public Result<List<ConsumptionRankingVo>> getConsumptionRanking(@RequestBody ConsumptionRankingDto dto) {
        if(dto == null || dto.getMerchantId() == null) {
            return new Result().error("商户ID不能为空!");
        }
        if(Strings.isNotBlank(dto.getStartDate()) && !RegularUtil.dateSlashRegular(dto.getStartDate())) {
            return new Result().error("开始日期错误(格式:yyyy-mm-dd)!");
        }
        if(Strings.isNotBlank(dto.getEndDate()) && !RegularUtil.dateSlashRegular(dto.getEndDate())) {
            return new Result().error("结束日期错误(格式:yyyy-mm-dd)!");
        }
        if(dto.getSortField() != null && !Arrays.asList(new Integer[] {0,1,2,3,4}).contains(dto.getSortField())) {
            return new Result().error("排序字段参数不正确!");
        }
        if(dto.getSort() != null && dto.getSort() != 0 && dto.getSort() != 1) {
            return new Result().error("排序参数不正确!");
        }
        return new Result().ok(statisticsService.getConsumptionRanking(dto));
    }

    @Login
    @PostMapping("getMerchantAccount")
    @ApiOperation("查询商户收支明细")
    public Result<List<MerchantAccountVo>> getMerchantAccount(@RequestBody MerchantAccountDto dto) {
        return new Result().ok(statisticsService.getMerchantAccount(dto));
    }

    @Login
    @GetMapping("getReturnDishesPage")
    @ApiOperation("商户端-退菜订单")
    public Result<PageData<ReturnDishesPageVo>> getReturnDishesPage(@RequestParam Map<String,Object> map){
        return new Result<PageData<ReturnDishesPageVo>>().ok(statisticsService.getReturnDishesPage(map));
    }

    @Login
    @GetMapping("getVisualizationRoom")
    @ApiOperation("商户端-可视化房间")
    public Result<List<VisualizationRoomVo>> getVisualizationRoom(@RequestParam Map<String,Object> map){
        return new Result<List<VisualizationRoomVo>>().ok(statisticsService.getVisualizationRoom(map));
    }

    @Login
    @GetMapping("daysTogetherPage")
    @ApiOperation("商户日汇总分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result<PageData<DaysTogetherPageDTO>> daysTogetherPage(@ApiIgnore @RequestParam Map<String, Object> params){
        return new Result<PageData<DaysTogetherPageDTO>>().ok(statisticsService.daysTogetherPage(params));
    }

    @Login
    @GetMapping("daysTogetherStat")
    @ApiOperation("商户日列表汇总")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "开始time", dataType="String",paramType = "query") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", dataType="String",paramType = "query")
    })
    public Result<DaysTogetherStatisticsVo> daysTogetherStat(@ApiIgnore @RequestParam Map<String, Object> params){
        return new Result<DaysTogetherStatisticsVo>().ok(statisticsService.daysTogetherStat(params));
    }

}