package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.AppVersionDTO;
import io.treasure.entity.AppVersionEntity;
import io.treasure.entity.mulitCouponBoundleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Mapper
public interface MulitCouponBoundleDao extends BaseDao<mulitCouponBoundleEntity> {


}
