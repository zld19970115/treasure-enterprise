package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.SlaveOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
public interface GoodService extends CrudService<GoodEntity, GoodDTO> {
    //根据商户id和菜品名称查询
    List getByNameAndMerchantId(String name,long martId);
    PageData<GoodDTO> listPage(Map<String,Object> params);
    //上架商品
    void on(long id,int status);
    //下架商品
    void off(long id,int status);
    //删除
    void remove(long id,int status);
    //根据id查询
    GoodDTO getByInfo(long id);

    GoodEntity getByid(long id);

    List<SlaveOrderEntity> getRefundGoods(String orderId,long goodId);
    PageData<GoodDTO>  sortingPage (Map<String, Object> params);
}