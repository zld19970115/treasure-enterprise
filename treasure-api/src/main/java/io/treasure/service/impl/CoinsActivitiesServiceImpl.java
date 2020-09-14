package io.treasure.service.impl;

import io.treasure.dao.CoinsActivitiesDao;
import io.treasure.entity.CoinsActivitiesEntity;
import io.treasure.service.CoinsActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;

public class CoinsActivitiesServiceImpl implements CoinsActivitiesService {

    @Autowired
    private CoinsActivitiesDao coinsActivitiesDao;

    public CoinsActivitiesEntity getCoinsActivityById(Long id){
        if(id == null)  id = 1L;
        return coinsActivitiesDao.selectById(id);
    }

    public CoinsActivitiesEntity initatorDefaultEntity(CoinsActivitiesEntity coinsActivitiesEntity){
        return null;
    }
    public boolean validityCoinsActivity(CoinsActivitiesEntity coinsActivitiesEntity){
        return false;
    }




}
