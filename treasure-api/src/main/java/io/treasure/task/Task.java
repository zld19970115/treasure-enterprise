package io.treasure.task;

import com.alipay.api.AlipayApiException;
import io.treasure.dao.SharingActivityExtendsDao;
import io.treasure.entity.SharingActivityExtendsEntity;
import io.treasure.service.CoinsActivitiesService;
import io.treasure.service.CouponForActivityService;
import io.treasure.service.MasterOrderService;
import io.treasure.task.item.*;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;

@Component
public class Task {

    @Autowired
    private OrderClear orderClear;
    @Autowired
    private OrderForBm orderForBm;
    @Autowired
    private ClientMemberGradeAssessment clientMemberGradeAssessment;
    @Autowired
    private WithdrawCommissionForMerchant withdrawCommissionForMerchant;
    @Autowired
    private CoinsActivity coinsActivity;
    @Autowired
    private ReseverRoomRecord reseverRoomRecord;
    @Autowired
    CouponForActivityService couponForActivityService;
    @Autowired(required = false)
    SharingActivityExtendsDao sharingActivityExtendsDao;
    @Autowired
    private CoinsActivitiesService coinsActivitiesService;
    @Autowired
    private AutoCancelOrders autoCancelOrders;
    @Autowired
    Tmp tmp;

    //处理次数记录
    private int taskInProcess = 0;
    private String resetTaskCounterTime = "2020-09-09 06:00:00";
    int n= 0;

    @Scheduled(fixedDelay = 10000)
    public void TaskManager() throws Exception {
        //0,复位所有定时任务
        if(TimeUtil.isOnTime(TimeUtil.simpleDateFormat.parse(resetTaskCounterTime),15)){
            resetAllCounter();
        }

        orderForBm.getOrderByYwy();
        //1,自动清台任务+加销奖励
//        if(orderClear.isInProcess() == false && orderClear.getTaskCounter()<2 && TimeUtil.isClearTime())
//            orderClear.clearOrder();

        //更新用户级别相关信息
        if(clientMemberGradeAssessment.isInProcess() == false && clientMemberGradeAssessment.isOnTime() && orderClear.getTaskCounter()<1){
            clientMemberGradeAssessment.updateGrade(20);
        }
        //自动执行用户提现相关操作----给商家返佣定时业务
        if(!withdrawCommissionForMerchant.isInProcess())
            withdrawCommissionForMerchant.startWithdrarw();

        //更新虚拟抢红钯奖池
        coinsActivitiesService.updateVisualJackpot();
        //发送提醒短信，提醒抢红包
//        if(coinsActivity.isOntime() && !coinsActivity.isInProcess() && coinsActivity.getTaskCounter()==0){
//            System.out.println("第"+n+"次");
//            coinsActivity.sentMsgToClientUsers();
//        }
        //生成包房记录相关
        if(reseverRoomRecord.isOntime() && !reseverRoomRecord.isInProcess() && reseverRoomRecord.getTaskCounter()<1){
            System.out.println("定时生成房间记录！！");
            reseverRoomRecord.checkReserverRoomRecord();
        }
        //更新计数器

        autoCancelOrders.updateDelayCounter();
        if(!autoCancelOrders.isInProcess())
            autoCancelOrders.execRefund(this.getClass());

//        if(!tmp.isInProcess()){
//            tmp.tmp1();
//        }


    }

    //=========================基本状态锁定===============================
    public boolean isInProcess(){
        if(taskInProcess >0)
            return true;
        return false;
    }
    public void unlockTask(){
        taskInProcess = 0;
    }
    public void lockedTask(){
        System.out.println("schedule module is running ... ...");
        taskInProcess++;
    }

    public void resetAllCounter(){
        orderClear.resetTaskCounter();
        clientMemberGradeAssessment.resetAllTaskCounter();
        withdrawCommissionForMerchant.resetAllTaskCounter();
        coinsActivity.resetAllTaskCounter();
        reseverRoomRecord.resetAllTaskCounter();
    }


}
