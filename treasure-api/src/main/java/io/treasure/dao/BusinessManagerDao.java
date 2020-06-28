package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.ClientUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessManagerDao  extends BaseDao<BusinessManagerEntity> {
    List<BusinessManagerDTO> getByNameAndPassWord(String realName, String passWord);
}
