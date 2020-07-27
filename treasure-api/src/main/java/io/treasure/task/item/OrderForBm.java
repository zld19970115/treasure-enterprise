package io.treasure.task.item;

import io.treasure.common.sms.SMSConfig;
import io.treasure.dao.BusinessManagerDao;
import io.treasure.dao.ClearOrderDao;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.MerchantDao;
import io.treasure.dto.OrderDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.BusinessManagerTrackRecordEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import io.treasure.task.TaskCommon;
import io.treasure.task.TaskSock;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.DistributionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
@Service
public class OrderForBm  extends TaskSock {

    @Autowired
    private MasterOrderDao masterOrderDao;
    @Autowired
    private BusinessManagerDao businessManagerDao;
    @Autowired
    private SMSConfig smsConfig;
    @Autowired
    private MerchantDao merchantDao;
    //每日凌晨5点进行清台操作
    /**     因为清掉的是此前24小时的订单有可能会带来一个问题就是前几个小时的订单线下未完成的，被清掉了      */
    public void getOrderByYwy() {
        lockedProcessLock();
        updateTaskCounter();  //更新执行程序计数器
        List<OrderDTO> orderDTOS = masterOrderDao.selectForBm();
        for (OrderDTO orderDTO : orderDTOS) {
            BusinessManagerTrackRecordEntity businessManagerTrackRecordEntity = businessManagerDao.selectByMartId(orderDTO.getMerchantId());
            BusinessManagerEntity businessManagerEntity = businessManagerDao.selectById(businessManagerTrackRecordEntity.getBmId());
            MerchantEntity merchantEntity = merchantDao.selectById(orderDTO.getMerchantId());
            SendSMSUtil.MerchantsToBm(businessManagerEntity.getMobile(),orderDTO.getOrderId(), merchantEntity.getName(), smsConfig);
            masterOrderDao.updateSmsStatus(orderDTO.getOrderId());
        }
        freeProcessLock();  //释放程序锁
    }


}
