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
import io.treasure.service.OrderRewardWithdrawRecordService;
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
        @Autowired
        private MerchantSalesRewardService merchantSalesRewardService;
        @Autowired
        private OrderRewardWithdrawRecordService orderRewardWithdrawRecordService;

        private boolean forceRunOnce = false;

        public void startWithdrarw() throws ParseException, AlipayApiException {
                lockedProcessLock();
                if(!isOnTime() || getTaskCounter() > 0)
                        return;
                updateTaskCounter();  //更新执行程序计数器

                UpdateCommissionRecord();//更新记录内容
                /*
                QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("sum(reward_value) as reward_value");
                queryWrapper.eq("cash_out_status",1);//未提现
                queryWrapper.eq("audit",0);//未进行审核

                List<MerchantSalesRewardRecordEntity> entities = merchantSalesRewardRecordDao.selectList(queryWrapper);
                Integer size=entities.size();

                for(int i=0;i<entities.size();i++){
                        MerchantSalesRewardRecordEntity entity = entities.get(i);
                        Integer method = entity.getMethod();

                        String commissionId = entity.getId()+"";
                        Long merchantId = entity.getMId();
                        //Integer amount = entity.getRewardValue();

                        if(method == 2){
                                //userWithdrawService.AliMerchantCommissionWithDraw(entity);
                            }else{
                                //userWithdrawService.wxMerchantCommissionWithDraw(entity);
                            }
                        System.out.println("withdraw commission - id"+entity.getId());
                }
                */
                forceRunOnce = false;
                freeProcessLock();
        }

        public boolean isOnTime() throws ParseException {
                if(forceRunOnce == true)
                        return true;

              String dString = "2020-01-01 00:00:00";
              Date parse = TimeUtil.simpleDateFormat.parse(dString);
             return TimeUtil.isOnTime(parse,10);
        }


        //每天执行一次
        public void UpdateCommissionRecord() throws ParseException {
                orderRewardWithdrawRecordService.execCommission();
        }

}
