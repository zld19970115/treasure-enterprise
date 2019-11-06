package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dto.EvaluateDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.impl.MerchantServiceImpl;
import io.treasure.service.impl.StatisticsServiceImpl;
import io.treasure.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import springfox.documentation.annotations.ApiIgnore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
            map.put("total_cash",merchantEntity.getTotalCash());
            //查询商家已提现金额
            map.put("already_cash",merchantEntity.getAlreadyCash());
            //查询商家未提现金额
            map.put("not_cash",merchantEntity.getNotCash());

        }
        map.put("todayOrder",todayOrder); //查询今日订单
        map.put("todayReserve",todayReserve); //查询今日预定订单
        map.put("todayQuit",todayQuit); //查询今日退订订单
        map.put("todayMoney",todayMoney);    //查询今日实际收入

        map.put("assignOrder",assignOrder); //查询指定日期得全部订单
        map.put("assignReserve",assignReserve); //查询指定日期得预定订单
        map.put("assignQuit",assignQuit);  //查询指定日期得退订订单
        map.put("assignMoney",assignMoney);  //查询指定日期得实际收入

        map.put("monthOrder",monthOrder); //查询本月全部订单
        map.put("monthReserve",monthReserve);   //查询本月全部预定订单
        map.put("monthQuit",monthQuit); //查询本月全部退订订单
        map.put("monthMoney",monthMoney);   //查询本月实际收入
        return new Result().ok(map);
    }
}