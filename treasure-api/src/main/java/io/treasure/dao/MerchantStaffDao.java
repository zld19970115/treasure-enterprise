package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantStaffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Mapper
public interface MerchantStaffDao extends BaseDao<MerchantStaffEntity> {

    void addOne(@Param("mchId") Long mchId,
                @Param("mobile") String mobile,
                @Param("sType") Integer sType,
                @Param("status") Integer status,
                @Param("tmpCode") String code);

    void updateCodeByMobile(@Param("mobile") String mobile,
                      @Param("status") Integer status,
                      @Param("tmpCode") String code);

    List<MerchantStaffEntity> getList(@Param("mchId") Long mchId,
                               @Param("status") Integer status,
                               @Param("mobile") String mobile);

    void delOne(@Param("mchId") Long mchId,
                @Param("mobile") String mobile);

}
