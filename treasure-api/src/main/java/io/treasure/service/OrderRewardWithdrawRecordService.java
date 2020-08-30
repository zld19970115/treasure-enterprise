package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dto.MerchantDTO;
import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import io.treasure.utils.TimeUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public interface OrderRewardWithdrawRecordService {

    boolean addRecord(MasterOrderEntity entity);
    boolean isExistByOrderId(String orderId);
    void execCommission() throws ParseException;
    boolean updateMerchantSalesRewardRecord(MerchantDTO merchantDTO) throws ParseException;
    boolean updateCopiedStatus(List<Long> ids);
}
