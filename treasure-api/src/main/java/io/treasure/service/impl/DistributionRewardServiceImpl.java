package io.treasure.service.impl;

import io.treasure.dao.DistributionRewardDao;
import io.treasure.dao.DistributionRewardLogDao;
import io.treasure.dao.SharingActivityDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.DistributionRelationshipEntity;
import io.treasure.entity.DistributionRewardLogEntity;
import io.treasure.entity.SharingActivityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Date;

public class DistributionRewardServiceImpl {

    @Autowired
    private ClientUserServiceImpl clientUserService;

    @Autowired
    private SharingActivityDao sharingActivityDao;

    @Autowired
    private DistributionRewardDao distributionRewardDao;
    @Autowired
    private DistributionRewardLogDao distributionRewardLogDao;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public Boolean binding(int saId,String mobileMaster,String mobileSlaver) {
        ClientUserEntity userByPhone = clientUserService.getUserByPhone(mobileMaster);
        ClientUserEntity slaverUserByPhone = clientUserService.getUserByPhone(mobileSlaver);
        SharingActivityEntity sharingActivityEntity = sharingActivityDao.selectById(saId);
        if (userByPhone==null || slaverUserByPhone==null){
            return true;
        }
        DistributionRelationshipEntity distributionRelationshipEntity = distributionRewardDao.selectByslaverUser(mobileSlaver);
        if (distributionRelationshipEntity!=null){
            return true;
        }
        DistributionRelationshipEntity distributionRelation = new DistributionRelationshipEntity();
        distributionRelation.setMobileMaster(mobileMaster);
        distributionRelation.setMobileSlaver(mobileSlaver);
        distributionRelation.setStatus(1);
        distributionRelation.setUnionStartTime(new Date());
        distributionRelation.setUnionExpireTime(sharingActivityEntity.getCloseDate());

        try{
            distributionRewardDao.updateById(distributionRelation);
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        return true;
    }
    public Boolean distribution(int saId,String mobileMaster,String mobileSlaver,int referencesTotal) {
        ClientUserEntity userByPhone = clientUserService.getUserByPhone(mobileMaster);
        ClientUserEntity slaverUserByPhone = clientUserService.getUserByPhone(mobileSlaver);
        int radio =  distributionRewardDao.selectRadio(saId);
        if (userByPhone==null || slaverUserByPhone==null){
            return true;
        }
        DistributionRelationshipEntity distributionRelationshipEntity = distributionRewardDao.selectByslaverUser(mobileSlaver);
        if (distributionRelationshipEntity==null){
            return true;
        }
        DistributionRewardLogEntity distributionRewardLogEntity = new DistributionRewardLogEntity();
        distributionRewardLogEntity.setConsume_time(new Date());
        distributionRewardLogEntity.setMobile_master(mobileMaster);
        distributionRewardLogEntity.setMobile_slaver(mobileSlaver);
        distributionRewardLogEntity.setReferences_total(referencesTotal);
        distributionRewardLogEntity.setReward_amount(referencesTotal*radio/100);
        distributionRewardLogEntity.setReward_ratio(radio);
        int i = referencesTotal * radio / 100 / 100;
        BigDecimal coin = userByPhone.getCoin();
        BigDecimal a = new BigDecimal(i);
        BigDecimal newCoin = a.add(coin);
        userByPhone.setCoin(newCoin);
        try{
            distributionRewardLogDao.updateById(distributionRewardLogEntity);
            clientUserService.updateById(userByPhone);
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
}
