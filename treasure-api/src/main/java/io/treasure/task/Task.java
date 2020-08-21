package io.treasure.task;

import io.treasure.task.item.*;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;

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
    //处理次数记录
    private int taskInProcess = 0;

    @Scheduled(fixedDelay = 5000)
    public void TaskManager(){

        //0,复位所有定时任务
        if(TimeUtil.resetTaskStatusTime()){
            resetAllCounter();
        }
        //1,自动清台任务+加销奖励
        if(orderClear.isInProcess() == false && orderClear.getTaskCounter()<2 && TimeUtil.isClearTime())
            orderClear.execOrderClear(true);

        if (orderForBm.isInProcess()==false){
            orderForBm.getOrderByYwy();
        }
        //更新用户级别相关信息
        if(clientMemberGradeAssessment.isInProcess() == false && clientMemberGradeAssessment.isOnTime() && orderClear.getTaskCounter()<1){
            clientMemberGradeAssessment.updateGrade(20);
        }
        //自动执行用户提现相关操作

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
    }


}
