package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.common.page.PageData;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.SlaveOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Mapper
public interface GoodDao extends BaseDao<GoodEntity> {
    List<GoodDTO> listPage(Map<String,Object> params);
    //根据菜品名称和商户id
    List getByNameAndMerchantId(@Param("name") String name,@Param("martId")long martId);
    //上架、下架商品
    void updateStatusById(@Param("id") long id,@Param("status")int status);
    //根据id查询
    GoodDTO getByInfo(long id);

    GoodEntity getByid(long id);
    List<SlaveOrderEntity> getRefundGoods(@Param("orderId") String orderId,@Param("goodId")long goodId);
    List<GoodDTO>  sortingPage (Map<String, Object> params);
}