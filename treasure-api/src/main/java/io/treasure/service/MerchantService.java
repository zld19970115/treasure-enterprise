package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MakeListDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.vo.AutoAceptVo;

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
    String selectOfficialMobile();
    void closeShop(long id ,int status);
    List<MerchantDTO>  selectByMartId(Map<String, Object> params);
    PageData<MerchantDTO> queryPage(Map<String, Object> params);
    void updateWX(String martId);
    PageData<MerchantDTO> queryALLMerchantBydistance(Map<String, Object> params);
    PageData<MerchantDTO> selectByUserlongitudeandlatitude(Map<String, Object> params);
    Result getOutside(String deliveryArea , int distribution, long martId);
    PageData<MerchantDTO> queryRoundPage(Map<String, Object> params);
    PageData<MerchantDTO> getMerchantByCategoryId(Map<String, Object> params);
    PageData<MerchantDTO> getMerchantByparty(Map<String, Object> params);
    PageData<MerchantDTO> getMerchantByspecial(Map<String, Object> params);
    PageData<MerchantDTO> getLikeMerchant(Map<String, Object> params);
    List<MerchantDTO> getListByOn();
    PageData<MerchantDTO>  merchantSortingPage (Map<String, Object> params);
    MerchantEntity getMerchantById(Long id);
    PageData<MerchantDTO>   martLike(Map<String, Object> params);
    Integer AuditMerchantStatus(Long id);
    List<MakeListDTO>  selectName(Map<String, Object> params);
    Integer updateRecommend(Long id, Integer recommend);
    Integer updateParty(Long id, Integer recommend);
    Integer updateSpecial(Long id, Integer recommend);
    PageData<MerchantDTO> search(Map<String, Object> params);
    PageData<MerchantDTO> searchMart(Map<String, Object> params);

    int attachCategoryByName(Long merchantId,String categoryName);
    int attachCategoryByNamePlus(String merchantName,String categoryName);

    void updateAutoAceptStatus(AutoAceptVo vo);

}
