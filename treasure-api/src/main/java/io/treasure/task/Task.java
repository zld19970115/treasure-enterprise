package io.treasure.task;

import io.treasure.dao.ScheduleMasterOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Task {


    int i=0;
    //@Scheduled(fixedDelay = 300000)
    public void TaskManager(){

        System.out.println("测试"+i++);
    }

}
