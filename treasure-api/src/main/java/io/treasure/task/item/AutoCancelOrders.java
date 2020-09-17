package io.treasure.task.item;

import io.treasure.dao.MasterOrderDao;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.MasterOrderService;
import io.treasure.task.base.TaskCommon;
import io.treasure.utils.TimeUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AutoCancelOrders extends TaskCommon {

    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;
    private Long verify = 15303690053L;

    private Long unPayExpireDate = 5*60*1000L;
    private Long paidExpireDate = 15*60*1000L;
    private static Long delayTimes = null;
    public static int delayCounter = 0;


    public void execRefund(Class<?> clazz) throws NoSuchMethodException{
        lockedProcessLock();
        if(delayTimes == null){
            updateScheduleDelayValue(clazz);
        }else
            if(delayCounter < delayTimes){
            //System.out.println("未达到次数"+delayCounter+","+delayTimes);
            freeProcessLock();
            return;
        }
        //System.out.println("scaning");
        List<MasterOrderEntity> orders = scanTimeOutOrders();
        for(int i=0;i<orders.size();i++){
            System.out.println(orders.get(i).toString());
            Long id = orders.get(i).getId();
            execAutoCancelOrder(id);
        }

        resetDelayCounter();
        freeProcessLock();
    }

    public List<MasterOrderEntity> scanTimeOutOrders(){
        Long date = new Date().getTime();
        Date unPayDate = new Date(date-unPayExpireDate);
        Date paidDate = new Date(date - paidExpireDate);

        List<MasterOrderEntity> orders =  masterOrderDao.scanAutoRefundOrders(unPayDate,paidDate);
        return orders;
    }
    public void execAutoCancelOrder(long id){
        masterOrderService.caleclUpdate(id,verify,new Date(),"time_out_refund",true);
    }

    public Long updateScheduleDelayValue(Class<?> clazz) throws NoSuchMethodException {

        Method targetMethod = clazz.getMethod("TaskManager");
        Scheduled annotation = targetMethod.getAnnotation(Scheduled.class);
        long delay = annotation.fixedDelay();

        delayTimes = unPayExpireDate/delay;
        return delay;
    }

    public void updateDelayCounter(){
        delayCounter++;
    }
    public void resetDelayCounter(){
        delayCounter = 0;
    }
}
