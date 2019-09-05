package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.UserGiftDao;
import io.treasure.dto.UserGiftDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserGiftEntity;
import io.treasure.service.UserGiftService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class UserGiftServiceImpl  extends CrudServiceImpl<UserGiftDao, UserGiftEntity, UserGiftDTO> implements UserGiftService {

    @Override
    public QueryWrapper<UserGiftEntity> getWrapper(Map<String, Object> params) {
        String id = (String)params.get("id");

        //状态
        String status = (String)params.get("status");

        //商户id
        QueryWrapper<UserGiftEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);

        return wrapper;
    }


    @Override
    public ClientUserEntity selectBynumber(String userNumber) {
        return baseDao.selectBynumber(userNumber);
    }

    @Override
    public UserGiftEntity selectStatus(String number,Integer password) {
        return baseDao.selectStatus(number,password);
    }

    @Override
    public void updateUnumberAndStatus(String userNumber, long id) {
        baseDao.updateUnumberAndStatus(userNumber,id);
    }

    @Override
    public void updateGift(BigDecimal money,long id) {
        baseDao.updateGift(money,id);
    }

    @Override
    public void insertGift(UserGiftDTO dto) {
        baseDao.insertGift(dto);
    }
}
