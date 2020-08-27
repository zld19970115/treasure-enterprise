package io.treasure.task.item;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.sms.SMSConfig;
import io.treasure.dao.MerchantDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.service.MerchantSalesRewardService;
import io.treasure.service.MerchantWithdrawService;
import io.treasure.service.UserWithdrawService;
import io.treasure.task.TaskCommon;
import io.treasure.utils.TimeUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 客户评级定时任务
 */
@Component
@Data
public class WithdrawCommissionForMerchant extends TaskCommon implements IWithdrawCommissionForMerchant{

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

        private boolean forceRunOnce = false;

        public void startWithdrarwxxxxx() throws ParseException, AlipayApiException {
                lockedProcessLock();
                if(!isOnTime() && getTaskCounter() == 0)
                        return;

                updateTaskCounter();  //更新执行程序计数器

                QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("sum(reward_value) as reward_value");
                queryWrapper.eq("cash_out_status",1);
                queryWrapper.eq("status",1);

                List<MerchantSalesRewardRecordEntity> entities = merchantSalesRewardRecordDao.selectList(queryWrapper);
                Integer size=entities.size();

                for(int i=0;i<entities.size();i++){
                        MerchantSalesRewardRecordEntity entity = entities.get(i);
                        Integer method = entity.getMethod();

                        String commissionId = entity.getId()+"";
                        Long merchantId = entity.getMId();
                        //Integer amount = entity.getRewardValue();

                        if(method == 2){
                                userWithdrawService.AliMerchantCommissionWithDrawxxxxx(entity);
                            }else{
                                userWithdrawService.wxMerchantCommissionWithDrawxxxxx(entity);
                            }
                        System.out.println("withdraw commission - id"+entity.getId());
                }

                forceRunOnce = false;
                freeProcessLock();
        }

        public boolean isOnTime() throws ParseException {
                if(forceRunOnce == true)
                        return true;

                String dString = "2020-08-25 22:00:00";
                MerchantSalesRewardEntity params = merchantSalesRewardService.getParams();

                Date parse = TimeUtil.simpleDateFormat.parse(dString);
                if(TimeUtil.isOnMothDay(parse)){
                     if(TimeUtil.isOnTime(parse,10)){
                             return true;
                     }
                }
                return false;
        }
        @Autowired
        private MerchantSalesRewardService merchantSalesRewardService;

        /*
                treasure.ct_merchant.commission_not_withdraw
                treasure.ct_merchant.commission_audit
                treasure.ct_merchant.commission_withdraw
                treasure.ct_merchant.commission_type
         */
}
