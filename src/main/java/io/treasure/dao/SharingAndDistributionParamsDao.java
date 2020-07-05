package io.treasure.dao;

import io.treasure.entity.SharingAndDistributionParams;

public interface SharingAndDistributionParamsDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SharingAndDistributionParams record);

    int insertSelective(SharingAndDistributionParams record);

    SharingAndDistributionParams selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SharingAndDistributionParams record);

    int updateByPrimaryKey(SharingAndDistributionParams record);
}