package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.BusinessManagerDao;
import io.treasure.dao.ClientUserDao;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.dto.ClientUserDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.BusinessManagerService;
import io.treasure.service.ClientUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BusinessManagerServiceImpl  extends CrudServiceImpl<BusinessManagerDao, BusinessManagerEntity, BusinessManagerDTO> implements BusinessManagerService {
    @Override
    public QueryWrapper<BusinessManagerEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<BusinessManagerDTO> getByNameAndPassWord(String realName, String passWord) {

      return   baseDao.getByNameAndPassWord(realName, passWord);
    }
}
