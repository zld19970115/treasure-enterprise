package io.treasure.service.impl;

import io.treasure.dao.SharingAndDistributionParamsDao;
import io.treasure.entity.SharingAndDistributionParamsEntity;
import io.treasure.service.SharingAndDistributionParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SharingAndDistributionParamsServiceImpl implements SharingAndDistributionParamsService {


    @Autowired(required = false)
    private SharingAndDistributionParamsDao dao;

    @Override
    public void setSharingDistributionParams(SharingAndDistributionParamsEntity sharingAndDistributionParamsEntity) {
        SharingAndDistributionParamsEntity sdbParams = getSharingDistributionParams();
        if(sdbParams == null){
            insertSharingDistributionParams(new SharingAndDistributionParamsEntity());
        }else{
            sharingAndDistributionParamsEntity.setId(sdbParams.getId());
            dao.updateById(sharingAndDistributionParamsEntity);
        }
    }

    @Override
    public SharingAndDistributionParamsEntity getSharingDistributionParams() {
        return dao.selectById(1);
    }

    @Override
    public void insertSharingDistributionParams(SharingAndDistributionParamsEntity sharingAndDistributionParamsEntity) {
        dao.insert(sharingAndDistributionParamsEntity);
    }
}
