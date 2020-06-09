package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.PowerLevelDao;
import io.treasure.dto.PowerContentDTO;
import io.treasure.dto.PowerLevelDTO;
import io.treasure.entity.PowerContentEntity;
import io.treasure.entity.PowerLevelEntity;
import io.treasure.service.PowerLevelService;
import io.treasure.utils.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public int insertPowerLevel(Map<String, Object> params, PowerContentDTO powerContentDTO) {
        int img = 0;
        Date date = new Date();
        Long userId = (Long) params.get("userId");
        int powerPeopleSum = (int)params.get("powerPeopleSum");
        int gift = (int)params.get("powerSum");
        params.put("powerOpenDate",date);
        PowerLevelDTO powerLevelDTO = baseDao.getPowerLevelByUserId(userId);
        if (powerLevelDTO == null){
            String list = RandomUtil.randowPower(powerContentDTO.getPowerSum(),powerContentDTO.getPowerPeopleSum(),1);
            params.put("ramdomNumber",list);
            System.out.println("1");
            img = baseDao.insertPowerLevel(params);
            baseDao.updatePowerSumByUserId(userId);

        } else if (powerLevelDTO != null && powerLevelDTO.getPowerSum()+1 <= powerPeopleSum) {
            baseDao.updatePowerSumByUserId(userId);
            if (powerLevelDTO.getPowerSum()+1 == powerPeopleSum && powerLevelDTO.getPowerFinish() == 0 ){
                img = 3;
                baseDao.updatePowerGainByUserId(userId);
                baseDao.updatePowerFinishByUserId(userId);
                params.put("powerAbortDate",date);
                baseDao.updatePowerAbortDateByUserId(userId, date);
            }
        }
        return img;
    }
    @Override
    public int insertPowerLevelx(Map<String, Object> params, PowerContentEntity powerContentEntity) {
        int img = 0;
        Date date = new Date();
        Long userId = (Long) params.get("userId");
        int powerPeopleSum = (int)params.get("powerPeopleSum");
        int gift = (int)params.get("powerSum");
        params.put("powerOpenDate",date);
        PowerLevelDTO powerLevelDTO = baseDao.getPowerLevelByUserId(userId);
        if (powerLevelDTO == null){
            String list = RandomUtil.randowPower(powerContentEntity.getPowerSum(),powerContentEntity.getPowerPeopleSum(),1);
            params.put("ramdomNumber",list);
            System.out.println("1");
            img = baseDao.insertPowerLevel(params);
            baseDao.updatePowerSumByUserId(userId);

        } else if (powerLevelDTO != null && powerLevelDTO.getPowerSum()+1 <= powerPeopleSum) {
            baseDao.updatePowerSumByUserId(userId);
            if (powerLevelDTO.getPowerSum()+1 == powerPeopleSum && powerLevelDTO.getPowerFinish() == 0 ){
                img = 3;
                baseDao.updatePowerGainByUserId(userId);
                baseDao.updatePowerFinishByUserId(userId);
                params.put("powerAbortDate",date);
                baseDao.updatePowerAbortDateByUserId(userId, date);
            }
        }
        return img;
    }


    @Override
    public PowerLevelDTO getPowerLevelByUserId(Long userId) {
        return baseDao.getPowerLevelByUserId(userId);
    }


    @Override
    public int updatePowerSumByUserId(Long userId) {
        return baseDao.updatePowerSumByUserId(userId);
    }

    @Override
    public int updatePowerGainByUserId(Long userId) {
        return baseDao.updatePowerGainByUserId(userId);
    }

    @Override
    public int updatePowerFinishByUserId(Long userId) {
        return baseDao.updatePowerFinishByUserId(userId);
    }

    @Override
    public int updatePowerAbortDateByUserId(Long userId, Date powerAbortDate) {
        return baseDao.updatePowerAbortDateByUserId(userId,powerAbortDate);
    }
}
