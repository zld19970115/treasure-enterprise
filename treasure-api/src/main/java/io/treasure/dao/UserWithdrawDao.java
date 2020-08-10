package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.entity.UserWithdrawEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserWithdrawDao extends BaseDao<UserWithdrawEntity> {
    List<UserWithdrawDTO> selectByUserIdAndStasus(long UserId);
    List<UserWithdrawDTO> listPage(Map<String, Object> params);
    List<UserWithdrawDTO> selectByUserIdAndalready(long UserId);
    List<UserWithdrawDTO> getMerchanWithDrawByMerchantId(Map<String, Object> params);
    UserWithdrawDTO getMerchanWithDrawByMerchantIdTotalRow(Map<String, Object> params);
}
