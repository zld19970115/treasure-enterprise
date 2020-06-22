package io.treasure.dao;

import io.treasure.entity.MerchantLeaderEntity;

import java.util.List;

public interface MerchantLeaderDao {

    List<MerchantLeaderEntity> selectLeaders(String bossMobile, MerchantLeaderEntity merchantLeaderEntity);

    void insertLeader(String bossMobile, MerchantLeaderEntity merchantLeaderEntity);

    void insertLeaders(String bossMobile, List<MerchantLeaderEntity> merchantLeaderEntities);

    void updateLeader(String bossMobile, MerchantLeaderEntity merchantLeaderEntity);

    void deleteLeader(String bossMobile, String leaderMobile);

    //selectCount
    int selectExistByMobile(String leaderMobile);

    int registLeader(MerchantLeaderEntity merchantLeaderEntity);
}
