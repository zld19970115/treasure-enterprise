package io.treasure.task.item;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.sms.SMSConfig;
import io.treasure.dao.MerchantDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.service.*;
import io.treasure.task.base.TaskCommon;
import io.treasure.task.item.interfaces.IWithdrawCommissionForMerchant;
import io.treasure.utils.TimeUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 自动更新用户销售返佣相关业务
 */
@Component
@Data
public class WithdrawCommissionForMerchant extends TaskCommon implements IWithdrawCommissionForMerchant {

        @Autowired(required = false)
        private MerchantWithdrawDao merchantWithdrawDao;

        @Autowired
        private MerchantWithdrawService merchantWithdrawService;

        @Autowired(required = false)
        private MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;

        @Autowired
        private SMSConfig smsConfig;

        @Autowired(required = false)
        private MerchantDao merchantDao;
        @Autowired
        private UserWithdrawService userWithdrawService;
        @Autowired
        private MerchantSalesRewardService merchantSalesRewardService;
        @Autowired
        private OrderRewardWithdrawRecordService orderRewardWithdrawRecordService;

        @Autowired
        private CommissionWithdrawService commissionWithdrawService;

        private boolean forceRunOnce = false;

        public void startWithdrarw() throws ParseException, AlipayApiException {
                lockedProcessLock();
                if(!isOnTime() || getTaskCounter() > 0)
                        return;
                updateTaskCounter();  //更新执行程序计数器

                UpdateCommissionRecord();//更新记录内容

                commissionWithdraw();//执行提现操作
                forceRunOnce = false;
                freeProcessLock();
        }

        public boolean isOnTime() throws ParseException {
                if(forceRunOnce == true)
                        return true;

              String dString = "2020-01-01 12:42:00";
              Date parse = TimeUtil.simpleDateFormat.parse(dString);
             return TimeUtil.isOnTime(parse,10);
        }


        //每天执行一次
        public void UpdateCommissionRecord() throws ParseException {
                orderRewardWithdrawRecordService.execCommission();
        }

        public void commissionWithdraw() throws AlipayApiException {
                QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("commission_volume");
                queryWrapper.eq("cash_out_status",1);//未提现
                queryWrapper.eq("audit_status",1);//同意提现

                List<MerchantSalesRewardRecordEntity> entities = merchantSalesRewardRecordDao.selectList(queryWrapper);

                for(int i=0;i<entities.size();i++){
                        MerchantSalesRewardRecordEntity entity = entities.get(i);
                        if(entity != null){
                                Integer method = entity.getMethod();

                                if(method == 2){
                                        commissionWithdrawService.aliMerchantCommissionWithDraw(entity);
                                }else if(method == 1){
                                        commissionWithdrawService.wxMerchantCommissionWithDraw(entity);
                                }
                                System.out.println("withdraw commission - id"+entity.getId());
                        }

                }
        }

}
