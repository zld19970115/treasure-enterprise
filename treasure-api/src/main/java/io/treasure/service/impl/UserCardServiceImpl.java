package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.UserCardDao;
import io.treasure.dto.CardInfoDTO;
import io.treasure.entity.CardInfoEntity;
import io.treasure.service.UserCardService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserCardServiceImpl extends CrudServiceImpl<UserCardDao, CardInfoEntity, CardInfoDTO> implements UserCardService {
    @Override
    public QueryWrapper<CardInfoEntity> getWrapper(Map<String, Object> params) {
        return null;
    }


    @Override
    public CardInfoEntity selectByIdAndPassword(long id, String password) {
        return baseDao.selectByIdAndPassword(id,password);
    }
}
