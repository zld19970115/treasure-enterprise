package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.UserPushDao;
import io.treasure.dto.UserPushDTO;
import io.treasure.entity.UserPushEntity;
import io.treasure.service.UserPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserPushServiceImpl extends CrudServiceImpl<UserPushDao, UserPushEntity, UserPushDTO> implements UserPushService {

    @Autowired
    private UserPushDao dao;

    @Override
    public QueryWrapper<UserPushEntity> getWrapper(Map<String, Object> params) {
        return null;
    }


    @Override
    public UserPushEntity selectByCid(String clientId) {
        return dao.selectByCid(clientId);
    }
}
