package io.treasure.service.impl;

import io.treasure.dao.SignedRewardSpecifyTimeDao;
import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import io.treasure.service.SignedRewardSpecifyTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignedRewardSpecifyTimeServiceImpl implements SignedRewardSpecifyTimeService {

    @Autowired(required = false)
    SignedRewardSpecifyTimeDao signedRewardSpecifyTimeDao;


    public SignedRewardSpecifyTimeEntity getSignedParamsById(Long id){
        Long qureyId = id==null?1L:id;
        SignedRewardSpecifyTimeEntity signedRewardSpecifyTimeEntity = signedRewardSpecifyTimeDao.selectById(qureyId);
        return signedRewardSpecifyTimeEntity;
    }




}
