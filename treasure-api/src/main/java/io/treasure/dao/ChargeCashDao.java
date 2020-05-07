package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.entity.ChargeCashEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Mapper
public interface ChargeCashDao  extends BaseDao<ChargeCashEntity> {
    ChargeCashDTO selectByCashOrderId(String cashOrderId);
}
