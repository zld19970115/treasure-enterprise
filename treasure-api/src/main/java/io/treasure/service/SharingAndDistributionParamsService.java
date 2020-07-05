package io.treasure.service;

import io.treasure.dao.SharingAndDistributionParamsDao;
import io.treasure.entity.SharingAndDistributionParamsEntity;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

public interface SharingAndDistributionParamsService {

    void setSharingDistributionParams(SharingAndDistributionParamsEntity sharingAndDistributionParamsEntity);

    SharingAndDistributionParamsEntity getSharingDistributionParams();

    void insertSharingDistributionParams(SharingAndDistributionParamsEntity sharingAndDistributionParamsEntity);

}
