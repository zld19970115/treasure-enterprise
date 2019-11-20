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

    PageData<MerchantDTO> queryPage(Map<String, Object> params) throws ParseException;

    PageData<MerchantDTO> queryAllPage(Map<String, Object> params);

    PageData<MerchantDTO> queryRoundPage(Map<String, Object> params) throws ParseException;
    List<MerchantDTO> getListByOn();

    MerchantEntity getMerchantById(Long id);
}