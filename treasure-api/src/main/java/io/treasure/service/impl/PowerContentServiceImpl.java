package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.PowerContentDao;
import io.treasure.dto.PowerContentDTO;
import io.treasure.entity.PowerContentEntity;
import io.treasure.service.PowerContentService;
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
        return baseDao.insertPowerContent(params);
    }

    @Override
    public List<PowerContentDTO> getPowerContentByUserId(Long userId) {
        return baseDao.getPowerContentByUserId(userId);
    }
}
