package io.treasure.controller;

import io.treasure.common.utils.Result;
import io.treasure.service.PayService;
import io.treasure.service.impl.PayServiceImpl;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/crc")
public class CashRefundController {

    @Autowired
    PayService payService;

    @PostMapping("cash_refund")
    @ResponseBody
    public Result CashRefund(@RequestParam(name ="order_no",defaultValue = "",required = true) String orderNo,
                             @RequestParam(name="refund_fee",defaultValue = "",required = true) String refund_fee,
                             @RequestParam(name = "good_id",defaultValue = "",required = false) Long goodId){
        //System.out.println(orderNo+","+refund_fee+","+goodId);
       Result result = payService.CashRefund(orderNo, refund_fee, goodId);
        //Result result=new Result();
        //return result.ok(true);
        return result;
    }
}
