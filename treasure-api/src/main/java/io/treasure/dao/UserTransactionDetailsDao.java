package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.UserTransactionDetailsDto;
import io.treasure.entity.UserTransactionDetailsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserTransactionDetailsDao extends BaseDao<UserTransactionDetailsEntity> {

    List<UserTransactionDetailsDto> pageList(Map<String,Object> map);

    UserTransactionDetailsDto pageTotalRow(Map<String,Object> map);

}
