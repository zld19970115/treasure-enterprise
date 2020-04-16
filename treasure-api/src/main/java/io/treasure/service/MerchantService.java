package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
public interface MerchantService extends CrudService<MerchantEntity, MerchantDTO> {
    //删除
    void remove(long id,int status);
    MerchantEntity getByNameAndCards(String name, String cards);
    //根据商户名称查询
    MerchantEntity  getByName(String name,int status);
    //闭店
    void closeShop(long id ,int status);
    List<MerchantDTO>  selectByMartId(Map<String, Object> params);
    PageData<MerchantDTO> queryPage(Map<String, Object> params);

    PageData<MerchantDTO> queryAllPage(Map<String, Object> params);

    PageData<MerchantDTO> queryRoundPage(Map<String, Object> params);
    PageData<MerchantDTO> getMerchantByCategoryId(Map<String, Object> params);
    PageData<MerchantDTO> getLikeMerchant(Map<String, Object> params);
    List<MerchantDTO> getListByOn();
    PageData<MerchantDTO>  merchantSortingPage (Map<String, Object> params);
    MerchantEntity getMerchantById(Long id);
}