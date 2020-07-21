package io.treasure.task;

import io.treasure.service.QRCodeService;
import io.treasure.service.impl.QRcodeServiceImpl;
import io.treasure.task.item.OrderClear;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Task {

    @Autowired
    private OrderClear orderClear;


    //处理次数记录
    private int taskInProcess = 0;

    @Scheduled(fixedDelay = 5000)
    public void TaskManager() throws Exception {
        if(isInProcess()) return;
        lockedTask();//

        //0,复位所有定时任务
        if(TimeUtil.resetTaskStatusTime()){
            resetAllCounter();
        }
        //1,自动清台任务+加销奖励
        if(orderClear.isInProcess() == false && orderClear.getTaskCounter()<2 && TimeUtil.isClearTime())
            orderClear.execOrderClear(true);

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


    public void sssest() throws Exception{
        long timeStamp = 1598917869000L;

        String s = TimeUtil.dateToStamp(new Date());
        long timeDiff = timeStamp - Long.parseLong(s);

        int day = 1000*60*60*24;
        int hour = 1000*60*60;
        int min = 1000*60;
        long days = timeDiff/day;
        long hours = (timeDiff%day)/hour;
        long mins = ((timeDiff%day)%hour)/min;
        long sec = ((timeDiff%day)%hour)%min;
        System.out.println("当前时间:"+days+"天,"+hours+"时，"+mins+"分，"+sec+"妙");
    }


}
