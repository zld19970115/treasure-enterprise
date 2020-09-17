package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MulitCouponBoundleNewDto;
import io.treasure.entity.MulitCouponBoundleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    List<MulitCouponBoundleEntity> selectRecord(Long clientUser_id);
    List<MulitCouponBoundleEntity> selectByStatus(Long clientUser_id);
    List<MulitCouponBoundleEntity> selectByMothod(@Param("userId")Long userId,@Param("Status")int Status);
    List<MulitCouponBoundleEntity> selectCOUNTByMothod(int Status);
    int selectByrule(int status);
    int selectByrule1(int status);
    List<MulitCouponBoundleNewDto> pageList(Map map);

}
