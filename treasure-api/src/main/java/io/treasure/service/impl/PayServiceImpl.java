package io.treasure.service.impl;

import io.treasure.dao.MasterOrderDao;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author super
 * @since 1.0.0 2019-09-08
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Resource
    MasterOrderDao masterOrderDao;

    @Override
    public Map<String, String> wxNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrder(total_amount,out_trade_no);
        if(masterOrderEntity==null){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "未找到订单");
            return mapRtn;
        }
        return null;
    }
}