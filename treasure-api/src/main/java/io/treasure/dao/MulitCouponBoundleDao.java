package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MulitCouponBoundleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Mapper
public interface MulitCouponBoundleDao extends BaseDao<MulitCouponBoundleEntity> {

    void updateStatusByIds(@Param("ids") List<Long> ids,@Param("consumeValue") BigDecimal consumeValue);
    void resumeStatusByIds(@Param("ids") List<Long> ids,@Param("consumeValue") BigDecimal consumeValue);

}
