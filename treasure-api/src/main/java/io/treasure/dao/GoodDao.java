package io.treasure.dao;


import io.treasure.common.dao.BaseDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantQueryDto;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.vo.SimpleDishesVo;
import io.treasure.vo.SpecifyMerchantVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Mapper
public interface GoodDao extends BaseDao<GoodEntity> {
    List<GoodDTO> listPage(Map<String, Object> params);

    //根据菜品名称和商户id
    List<GoodDTO> getByNameAndMerchantId(@Param("name") String name, @Param("martId") long martId);

    //上架、下架商品
    void updateStatusById(@Param("id") long id, @Param("status") int status);

    //根据id查询
    GoodDTO getByInfo(long id);

    GoodEntity getByid(long id);

    List<SlaveOrderEntity> getRefundGoods(@Param("orderId") String orderId, @Param("goodId") long goodId);

    List<GoodDTO> sortingPage(Map<String, Object> params);

    List<GoodDTO> selectEnableGoodsByMerchantIdForUserOnly(@Param("merchantId") long merchantId,
                                                           @Param("pageNum") int pageNum,
                                                           @Param("num") int num);

    List<SimpleDishesVo> selectDishesMenu(@Param("startLetter") String startLetter,
                                          @Param("page") int page,
                                          @Param("num") int num,
                                          @Param("inList") String inList);

    List<SimpleDishesVo> selectDishesMenuCount(@Param("startLetter") String startLetter);

    List<SpecifyMerchantVo> selectMchViaWholePrice(MerchantQueryDto dto);

    //数值除以总项数
    Integer selectMchCountViaWholePrice(MerchantQueryDto dto);


}