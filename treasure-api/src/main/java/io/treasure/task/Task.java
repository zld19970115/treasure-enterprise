package io.treasure.task;

import io.treasure.task.item.OrderClear;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Task {

    @Autowired
    private OrderClear orderClear;

    //处理次数记录
    private int taskInProcess = 0;

    //@Scheduled(fixedDelay = 5000)
    public void TaskManager(){
        if(isInProcess()) return;
        lockedTask();//

        //0,复位所有定时任务
        if(TimeUtil.resetTaskStatusTime()){
            resetAllCounter();
        }
        //1,自动清台任务+加销奖励
        if(orderClear.isInProcess() == false && orderClear.getTaskCounter()<2 && TimeUtil.isClearTime())
            orderClear.execOrderClear(true);




        unlockTask();
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
    }


}
