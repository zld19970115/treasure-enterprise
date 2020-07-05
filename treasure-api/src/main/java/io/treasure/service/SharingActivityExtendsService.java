package io.treasure.service;

import io.treasure.entity.SharingActivityExtendsEntity;

import java.util.List;

public interface SharingActivityExtendsService {

    SharingActivityExtendsEntity getExtendsInfoById(int saeId);

    void modifyExtendsInfoById(SharingActivityExtendsEntity entity);

    void insertExtendsInfo(SharingActivityExtendsEntity entity);
    void delExtendsInfoById(int saeId);
}
