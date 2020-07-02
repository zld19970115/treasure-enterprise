package io.treasure.schedule;

import org.springframework.scheduling.annotation.Scheduled;

public class ScheduleProcessor {

    private final int delay = 1000*60*5;
    private static boolean spStatus = false;//运行中状态


    //@Scheduled(fixedDelay = delay)
    public void schedules(){
        //超时自动退款

        //超时自动取消状态




    }
}
