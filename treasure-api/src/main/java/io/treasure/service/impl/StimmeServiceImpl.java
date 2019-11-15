package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.StimmeDao;
import io.treasure.dto.StimmeDTO;
import io.treasure.entity.StimmeEntity;
import io.treasure.service.StimmeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class StimmeServiceImpl  extends CrudServiceImpl<StimmeDao, StimmeEntity, StimmeDTO> implements StimmeService {
    @Override
    public QueryWrapper<StimmeEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<StimmeEntity> selectBymerchantId(Map<String, Object> params) {
        return baseDao.selectBymerchantId(params);
    }

    @Override
    public StimmeDTO selectByOrderId(String orderId) {
        return baseDao.selectByOrderId(orderId);
    }
}
