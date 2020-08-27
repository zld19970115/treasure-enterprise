package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.RecordGiftDao;
import io.treasure.dao.RecordNewsDao;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.dto.RecordNewsDTO;
import io.treasure.entity.RecordGiftEntity;
import io.treasure.entity.RecordNewsEntity;
import io.treasure.service.RecordGiftService;
import io.treasure.service.RecordNewsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecordNewsServiceImpl extends CrudServiceImpl<RecordNewsDao, RecordNewsEntity, RecordNewsDTO> implements RecordNewsService {
    @Override
    public QueryWrapper<RecordNewsEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public RecordNewsEntity selectByUandNid(Long id, Long nId, int type) {
        return baseDao.selectByUandNid(id,nId,type);
    }

    @Override
    public List<RecordNewsEntity> selectByUid(Long id, int type) {
        return baseDao.selectByUid(id,type);
    }
}
