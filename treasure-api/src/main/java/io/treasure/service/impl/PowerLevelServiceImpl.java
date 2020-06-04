package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.PowerLevelDao;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.entity.PowerLevelEntity;
import io.treasure.service.PowerLevelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力级别
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:48
 */
@Service
public class PowerLevelServiceImpl extends CrudServiceImpl<PowerLevelDao, PowerLevelEntity, PowerLevelDTO> implements PowerLevelService {


    @Override
    public QueryWrapper<PowerLevelEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public int insertPowerLevel(Map<String, Object> params) {
        return baseDao.insertPowerLevel(params);
    }

    @Override
    public PowerLevelDTO getPowerLevelByUserId(Long userId) {
        return baseDao.getPowerLevelByUserId(userId);
    }

    @Override
    public int updatePowerGiftByUserId(Long userId,int gift) {
        return baseDao.updatePowerGiftByUserId(userId,gift);
    }


    @Override
    public int updatePowerSumByUserId(Long userId) {
        return baseDao.updatePowerSumByUserId(userId);
    }
}
