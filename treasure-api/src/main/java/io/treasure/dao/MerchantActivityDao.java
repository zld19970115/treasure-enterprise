package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.entity.MerchantActivityEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@Mapper
public interface MerchantActivityDao extends BaseDao<MerchantActivityEntity> {
    //删除
    void updateStatusById(Long id, int status);
    List<MerchantActivityDTO> listPage(Map<String, Object> params);
}