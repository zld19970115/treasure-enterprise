package io.treasure.service.impl;

import io.treasure.dao.SharingActivityExtendsDao;
import io.treasure.entity.SharingActivityExtendsEntity;
import io.treasure.service.SharingActivityExtendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SharingActivityExtendsServiceImpl implements SharingActivityExtendsService {

    @Autowired(required = false)
    private SharingActivityExtendsDao dao;

    @Override
    public SharingActivityExtendsEntity getExtendsInfoById(int saeId) {
        return dao.selectById(saeId);
    }

    @Override
    public void modifyExtendsInfoById(SharingActivityExtendsEntity entity) {
        dao.updateById(entity);

    }

    @Override
    public void insertExtendsInfo(SharingActivityExtendsEntity entity) {
        dao.insert(entity);
    }

    @Override
    public void delExtendsInfoById(int saeId) {

    }
}
