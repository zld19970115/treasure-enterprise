package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantWithdrawEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
@Mapper
public interface MerchantWithdrawDao extends BaseDao<MerchantWithdrawEntity> {
    void updateStatusById(long id,int status);
}