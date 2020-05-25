package io.treasure.controller;

import cn.hutool.db.DaoTemplate;
import io.treasure.common.utils.Result;
import io.treasure.service.MasterOrderService;
import io.treasure.service.PayService;
import io.treasure.service.impl.PayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/crc")
public class CashRefundController {

    @Autowired
    PayService payService;

    @Autowired
    MasterOrderService masterOrderService;

    @GetMapping("/cash_refund")
    @ResponseBody
    public Result CashRefund(@RequestParam(name ="order_no",defaultValue = "",required = true) String orderNo,
                             @RequestParam(name="refund_fee",defaultValue = "",required = true) String refund_fee,
                             @RequestParam(name = "good_id",defaultValue = "",required = false) Long goodId){

       Result result = payService.CashRefund(orderNo, refund_fee, goodId);
        Result result1=null;
        if (result.success()) {
            boolean b = (boolean) result.getData();
            if (!b) {
                return new Result().error("退款失败！");
            }else{
                System.out.println("退款成功");
                try {
                    result1 = masterOrderService.orderRefundSuccess(orderNo, 8);
                    return result1;
                } catch (Exception e) {
                    e.printStackTrace();
                    //同意退款回调失败
                    System.out.println("同意退款后续操作异常，请及时处理，以免带来经济损失==========================!");
                    return new Result().error(result.getMsg());
                }
            }
        }
        return result1;
    }
}
