package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.entity.PowerLevelEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力级别
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:15
 */
@Mapper
public interface PowerLevelDao extends BaseDao<PowerLevelEntity> {

    /**
     * 添加助力信息
     */
    int insertPowerLevel(Map<String,Object> params);

    /**
     * 根据用户id查询助力级别
     */
    PowerLevelDTO getPowerLevelByUserId(Long userId);

    /**
     * 根据用户id更新助力代付金
     */
    int updatePowerGiftByUserId(Long userId,int gift);


    /**
     * 根据用户id更新助力人数
     */
    int updatePowerSumByUserId(Long userId);


}
