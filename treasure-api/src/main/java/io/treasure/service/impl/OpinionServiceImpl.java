package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.OpinionDao;
import io.treasure.dto.OpinionDTO;
import io.treasure.entity.OpinionEntity;
import io.treasure.service.OpinionService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpinionServiceImpl extends CrudServiceImpl<OpinionDao, OpinionEntity, OpinionDTO> implements OpinionService {
    @Override
    public QueryWrapper<OpinionEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public void insertOpinion(Map<String, Object> params) {
        baseDao.insertOpinion(params);
    }
}
