package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dto.EvaluateDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.impl.MerchantServiceImpl;
import io.treasure.service.impl.StatisticsServiceImpl;
import io.treasure.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result todayOrder(@RequestParam long merchantId) {
        Map map = new HashMap();
        //获取本日日期
        String format = DateUtil.getToday();
        //获取本月
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        //查询今日订单
        int todayOrder = statisticsService.todayOrder( format,merchantId);
        //查询今日预定订单
        int todayReserve = statisticsService.todayReserve( format,merchantId);
        //查询今日退订订单
        int todayQuit = statisticsService.todayQuit( format,merchantId);
        //查询今日实际收入
        double todayMoney = statisticsService.todayMoney(format, merchantId);
        //查询本月全部订单
        int monthOrder = statisticsService.monthOrder(month,merchantId);
        //查询本月全部预定订单
        int monthReserve = statisticsService.monthReserve(month,merchantId);
        //查询本月全部退订订单
        int monthQuit = statisticsService.monthQuit(month,merchantId);
        //查询本月实际收入
        Double monthMoney = statisticsService.monthMoney(month,merchantId);
        //查询全部实际收入
        Double allMoney = statisticsService.allMoney(merchantId);
        MerchantEntity merchantEntity = merchantService.selectById(merchantId);
        if (merchantEntity!=null){
            //查询商家可提现总额
            map.put("total_cash",merchantEntity.getTotalCash());
            //查询商家已提现金额
            map.put("already_cash",merchantEntity.getAlreadyCash());
            //查询商家未提现金额
            map.put("not_cash",merchantEntity.getNotCash());

        }
        //查询商家可提现总额
        map.put("todayOrder",todayOrder);
        map.put("todayReserve",todayReserve);
        map.put("todayQuit",todayQuit);
        map.put("todayMoney",todayMoney);
        map.put("monthOrder",monthOrder);
        map.put("monthReserve",monthReserve);
        map.put("monthQuit",monthQuit);
        map.put("monthMoney",monthMoney);
        return new Result().ok(map);
    }


}