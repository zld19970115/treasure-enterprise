package io.treasure.task.item;

import io.treasure.dao.ClearOrderDao;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import io.treasure.task.TaskCommon;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.DistributionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderClear extends TaskCommon implements IOrderClear {

    @Autowired(required = false)
    private ClearOrderDao clearOrderDao;

    @Autowired
    private DistributionRewardServiceImpl distributionRewardService;

    private List<DistributionVo> distributionVos = new ArrayList<>();//分类整理分销对象


    //每日凌晨5点进行清台操作
    /**     因为清掉的是此前24小时的订单有可能会带来一个问题就是前几个小时的订单线下未完成的，被清掉了      */
    @Override
    public void execOrderClear(boolean andDistributionReward) {
        lockedProcessLock();
        updateTaskCounter();  //更新执行程序计数器

        System.out.println("schedule：(clear)update merchant order status by complete ... ...");
        String[] specifyDateTime=null;
        try{
            specifyDateTime = TimeUtil.getFinishedUpdateTime();
        }catch (ParseException e){
            System.out.println("schedule: exception ... ...");
            resetTaskCounter(); //复位程序计数器
        }
        if(specifyDateTime == null){
            freeProcessLock();
            return;
        }


        System.out.println("time0"+specifyDateTime[0]+",time1"+specifyDateTime[1]);
        clearOrderDao.clearSlaveOrders(specifyDateTime[0],specifyDateTime[1]);
        clearOrderDao.clearRooms(specifyDateTime[0],specifyDateTime[1]);


        if(andDistributionReward){
            distributionReward(specifyDateTime[0],specifyDateTime[1]);
        }
        clearOrderDao.clearMasterOrders(specifyDateTime[0],specifyDateTime[1]);
        freeProcessLock();  //释放程序锁

    }

    //分销奖励
    public void distributionReward(String timeStart,String timeStop){
        List<DistributionVo> distributionVos = clearOrderDao.distributionList(timeStart,timeStop);

        if(distributionVos.size() != 0){
            distributionVos = groupByList(distributionVos);
        }
        for(int i=0;i<distributionVos.size();i++){
            DistributionVo distributionVo = distributionVos.get(i);
            int payMoneyInt = scaleHundredfoldAndParseInt(distributionVo.getPayMoney());
            if(payMoneyInt != 0)
                distributionRewardService.distribution(distributionVo.getContactNumber(),payMoneyInt);
        }
    }

    /**
     * 合并列表分组项
     * @param preTarget
     * @return
     */
    public List<DistributionVo> groupByList(List<DistributionVo> preTarget){
        distributionVos.clear();

        for(int i =0;i<preTarget.size();i++){
            int index = isExistItem(preTarget.get(i).getContactNumber());
            if(index != -1){
                BigDecimal currentAmount = distributionVos.get(index).getPayMoney();
                DistributionVo distributionVo = preTarget.get(i).addTotalMoney(currentAmount);
                distributionVos.set(index,distributionVo);
            }else{
                distributionVos.add(preTarget.get(i));
            }
        }
        return distributionVos;
    }
    public int isExistItem(String mobile){

        for(int i=0;i<distributionVos.size();i++){
            if(mobile == distributionVos.get(i).getContactNumber())
                return i;
        }

        return -1;
    }


    public int scaleHundredfoldAndParseInt(BigDecimal target) {
        if(target.compareTo(new BigDecimal("0")) <= 0)
            return 0;
        String tmp = target + "0000";
        int dotPosition = tmp.indexOf(".");
        String res = tmp.substring(0, dotPosition) + tmp.substring(dotPosition + 1, 5);
        return Integer.parseInt(res);
    }

    public void lockedClear(){

    }
    public void freeClear(){

    }
    public boolean clearStatus(){
return false;
    }
}
