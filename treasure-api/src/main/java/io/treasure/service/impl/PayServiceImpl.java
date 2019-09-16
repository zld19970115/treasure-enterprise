package io.treasure.service.impl;

import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.enm.Constants;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.service.PayService;
import io.treasure.service.SlaveOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    SlaveOrderDao slaveOrderDao;

    @Autowired
    SlaveOrderService slaveOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> wxNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrder(out_trade_no);
        if(masterOrderEntity.getPayMoney().compareTo(total_amount)!=0){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;
        }
        if(masterOrderEntity==null){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
        masterOrderEntity.setPayMode(Constants.PayMode.WXPAY.getValue());
        masterOrderEntity.setPayDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        if(masterOrderEntity.getReservationType()!=Constants.ReservationType.ONLYROOMRESERVATION.getValue()){
            List<SlaveOrderEntity> slaveOrderEntitys=slaveOrderService.selectByOrderId(out_trade_no);
            if(slaveOrderEntitys==null){
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单菜品】");
                return mapRtn;
            }else{
                slaveOrderEntitys.forEach(slaveOrderEntity -> {
                    slaveOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                });
                boolean b=slaveOrderService.updateBatchById(slaveOrderEntitys);
                if(!b){
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【更新菜品】");
                    return mapRtn;
                }
            }
        }
        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }
}