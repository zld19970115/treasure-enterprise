package io.treasure.service.impl;

import io.treasure.dao.ClientLevelRuleDao;
import io.treasure.entity.ClientLevelRuleEntity;
import io.treasure.utils.UpdateUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientLevelRuleServiceImpl {

    @Autowired(required = false)
    private ClientLevelRuleDao clientLevelRuleDao;

    //基本参数设置

    public ClientLevelRuleEntity getClientLevelRuleEntity(){

        //ClientLevelRuleEntity one = clientLevelRuleDao.getLevelRule();
        return null;
    }

    public void updateClientLevelRuleEntity(ClientLevelRuleEntity clientLevelRuleEntity){
       // ClientLevelRuleEntity one = clientLevelRuleDao.getLevelRule();

    }



}
