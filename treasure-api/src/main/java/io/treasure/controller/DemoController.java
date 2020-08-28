package io.treasure.controller;

import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.impl.OrderRewardWithdrawRecordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private OrderRewardWithdrawRecordServiceImpl orderRewardWithdrawRecordService;


    @Login
    @PostMapping("insert")
    @ApiOperation("新增")
    public Result insert() {

        MasterOrderEntity m = new MasterOrderEntity();
        m.setOrderId("1111");
        m.setPayMoney(new BigDecimal("23.00"));
        m.setPayCoins(new BigDecimal("12.00"));
        m.setTotalMoney(new BigDecimal("12.00"));
        m.setPlatformBrokerage(new BigDecimal("12.00"));
        m.setMerchantId(12312l);

        //orderRewardWithdrawRecordService.addPreWithdrawRecord(m);

        return new Result().ok("success");
    }

//    @Login
//    @PostMapping("complete_update")
//    @ApiOperation("成功状态更新")
//    public Result update_withdraw(@RequestBody List<Long> ids) {
//        orderRewardWithdrawRecordService.updateWithdrawFlagById(ids, EOrderRewardWithdrawRecord.NOT_WITHDRAW);
//        return new Result().error(1);
//    }
//
//    @Login
//    @PostMapping("failure_update")
//    @ApiOperation("失败状态更新")
//    public Result update_not_withdraw(@RequestBody List<Long> ids) {
//        orderRewardWithdrawRecordService.updateWithdrawFlagById(ids,EOrderRewardWithdrawRecord.WITHDRAW_COMPLETE);
//        return new Result().error(1);
//    }

}
