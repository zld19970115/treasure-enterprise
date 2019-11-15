package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.StimmeDao;
import io.treasure.dto.StimmeDTO;
import io.treasure.entity.StimmeEntity;
import io.treasure.service.StimmeService;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class StimmeServiceImpl  extends CrudServiceImpl<StimmeDao, StimmeEntity, StimmeDTO> implements StimmeService {
    @Override
    public QueryWrapper<StimmeEntity> getWrapper(Map<String, Object> params) {
        return null;
    }
}
