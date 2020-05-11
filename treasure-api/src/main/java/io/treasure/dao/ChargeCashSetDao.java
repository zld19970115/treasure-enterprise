package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.ChargeCashSetDTO;
import io.treasure.entity.ChargeCashSetEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 现金充值设置表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Mapper
public interface ChargeCashSetDao  extends BaseDao<ChargeCashSetEntity> {
    ChargeCashSetEntity selectByCash(BigDecimal cash);
    List<ChargeCashSetDTO> select();
}
