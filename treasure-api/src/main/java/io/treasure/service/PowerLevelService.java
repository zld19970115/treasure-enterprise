package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.entity.PowerLevelEntity;
import io.treasure.entity.RecordGiftEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力级别
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:46
 */
@Service
public interface PowerLevelService extends CrudService<PowerLevelEntity, PowerLevelDTO> {
    /**
     * 添加助力信息
     */
    int insertPowerLevel(Map<String,Object> params);

    /**
     * 根据用户id查询助力级别
     */
    PowerLevelDTO getPowerLevelByUserId(Long userId);

    /**
     * 根据用户id更新一级助力
     */
    int updatePowerGiftByUserId(Long userId,int gift);


    /**
     * 根据用户id更新助力人数
     */
    int updatePowerSumByUserId(Long userId);



}
