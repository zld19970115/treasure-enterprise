package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantAdvertExtendDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.MerchantAdvertExtendEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 商户广告位推广
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface MerchantAdvertExtendDao extends BaseDao<MerchantAdvertExtendEntity> {
    List<MerchantAdvertExtendDTO> listPage(Map<String, Object> params);
}