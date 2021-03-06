package io.treasure.task.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.task.base.TaskCommon;
import io.treasure.task.item.interfaces.ICoinsActivity;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 宝币活动定时发送提醒短信业务--暂时停用
 */
@Component
public class CoinsActivity extends TaskCommon implements ICoinsActivity {

    @Autowired(required = false)
    private ClientUserDao clientUserDao;
    @Autowired
    private SendSMSUtil sendSMSUtil;
    public void sentMsgToClientUsers(){
        lockedProcessLock();
        System.out.println("计数0"+getTaskCounter());
        updateTaskCounter();  //更新执行程序计数器
        System.out.println("计数1"+getTaskCounter());
        List<ClientUserEntity> clientUsers = getClientUsers();

        for(int i=0;i<clientUsers.size();i++){
            String mobile = clientUsers.get(i).getMobile();
            if(mobile != null){
                if (!mobile.contains("已注销") && mobile != null){
                    System.out.println("邀请参加抢宝币活动  "+mobile+"  ==!");
                    //sendSMSUtil.commissionNotify(mobile,"平台","无限", SendSMSUtil.CommissionNotifyType.COINS_ACTIVITY_NOTIFY);
                }
            }
        }

        System.out.println("处理完毕!!!");
        freeProcessLock();
    }

    public List<ClientUserEntity> getClientUsers(){
        QueryWrapper<ClientUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,mobile");
        List<ClientUserEntity> clientUserEntities = clientUserDao.selectList(queryWrapper);
        return clientUserEntities;
    }

    @Autowired
    private CouponForActivityService couponForActivityService;
    public boolean isOntime(){
        SignedRewardSpecifyTimeEntity paramsById = couponForActivityService.getParamsById(null);
        Date startPmt = paramsById.getStartPmt();

        boolean onTime = TimeUtil.isOnTime(startPmt, 30);
        return onTime;
    }

}
