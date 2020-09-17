package io.treasure.task.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.task.base.TaskCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.beans.Transient;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@Component
public class Tmp extends TaskCommon {

    @Autowired(required = false)
    private ClientUserDao clientUserDao;
    @Autowired
    CouponForActivityService couponForActivityService;

    public List<ClientUserEntity> tmp0(){
        QueryWrapper<ClientUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("balance",0);
        queryWrapper.select("id,mobile,balance");
        List<ClientUserEntity> clientUserEntities = clientUserDao.selectList(queryWrapper);

        return clientUserEntities;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void tmp1() throws ParseException {

        lockedProcessLock();
        //Long clientId,BigDecimal bd,Integer method,Integer validity, ESharingRewardGoods.ActityValidityUnit actityValidityUnit
        List<ClientUserEntity> clientUserEntities = tmp0();
        for(int i=0;i<clientUserEntities.size();i++){
            ClientUserEntity entity = clientUserEntities.get(i);
            if(entity.getBalance().doubleValue()>0 && entity.getMobile().length()==11){
                System.out.println("id:"+entity.getId()+";mobile:"+entity.getMobile()+";balance:"+entity.getBalance());
                try {
                    couponForActivityService.insertClientActivityRecord(entity.getId(), entity.getBalance(),
                            2, 1, ESharingRewardGoods.ActityValidityUnit.UNIT_MONTHS);

                    entity.setBalance(new BigDecimal("0"));
                    clientUserDao.updateById(entity);
                }catch (Exception e){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    e.printStackTrace();
                }
            }else{
                System.out.println("不符合要求");
            }
        }
        System.out.println("处理完毕... ...");
    }
}
