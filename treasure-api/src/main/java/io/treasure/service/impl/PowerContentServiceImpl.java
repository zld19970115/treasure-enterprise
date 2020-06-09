package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.PowerContentDao;
import io.treasure.dto.PowerContentDTO;
import io.treasure.entity.PowerContentEntity;
import io.treasure.service.PowerContentService;
import io.treasure.utils.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力信息
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:47
 */
@Service
public class PowerContentServiceImpl extends CrudServiceImpl<PowerContentDao, PowerContentEntity, PowerContentDTO> implements PowerContentService {


    @Override
    public QueryWrapper<PowerContentEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public int insertPowerContent(Map<String, Object> params) {
        if (params.get("merchandiseId") == null){
            params.put("merchandiseId",1);
        }else if (params.get("goodId") == null){
            params.put("goodId",1);
        }
        Long powerlevelId = Long.valueOf(RandomUtil.random8());
        params.put("powerlevelId",powerlevelId);
        return baseDao.insertPowerContent(params);
    }

    @Override
    public PowerContentDTO getPowerContentByUserId(Long powerlevelId) {
        return baseDao.getPowerContentByUserId(powerlevelId);
    }

    @Override
    public PowerContentEntity getPowerContentByPowerLevelId(Long powerlevelId) {
        QueryWrapper<PowerContentEntity> powerContentEntityQueryWrapper = new QueryWrapper<>();
        powerContentEntityQueryWrapper.eq("powerlevel_id",powerlevelId);
        return baseDao.selectOne(powerContentEntityQueryWrapper);
    }
}
