package io.treasure.task.item;

import io.treasure.common.sms.SMSConfig;
import io.treasure.dao.BusinessManagerDao;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.MerchantDao;
import io.treasure.dto.OrderDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.BusinessManagerTrackRecordEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.task.base.TaskSock;
import io.treasure.utils.SendSMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderForBm  extends TaskSock {

    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;
    @Autowired(required = false)
    private BusinessManagerDao businessManagerDao;
    @Autowired
    private SMSConfig smsConfig;
    @Autowired(required = false)
    private MerchantDao merchantDao;
    //每日凌晨5点进行清台操作
    /**     因为清掉的是此前24小时的订单有可能会带来一个问题就是前几个小时的订单线下未完成的，被清掉了      */
    public void getOrderByYwy() {
        System.out.println("执行了+++++++++++++++++++++++++++++");
            //更新执行程序计数器
            List<OrderDTO> orderDTOS = masterOrderDao.selectForBm();
            for (OrderDTO orderDTO : orderDTOS) {
                BusinessManagerTrackRecordEntity businessManagerTrackRecordEntity = businessManagerDao.selectByMartId(orderDTO.getMerchantId());
                if(businessManagerTrackRecordEntity!=null){
                    BusinessManagerEntity businessManagerEntity = businessManagerDao.selectById(businessManagerTrackRecordEntity.getBmId());
                    MerchantEntity merchantEntity = merchantDao.selectById(orderDTO.getMerchantId());
                    System.out.println(businessManagerEntity.getMobile()+orderDTO.getOrderId()+ merchantEntity.getName());
                    boolean b = SendSMSUtil.MerchantsToBm(businessManagerEntity.getMobile(), orderDTO.getOrderId(), merchantEntity.getName(), smsConfig);
                    System.out.println(b +"发短信+++++++++++++++++++++++++++++++++++++++++++++++++++");
                    if (b==true){
                        masterOrderDao.updateSmsStatus(orderDTO.getOrderId());
                    }

                }

            }








        }



}
