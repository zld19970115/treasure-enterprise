package io.treasure.dao;


import io.treasure.common.dao.BaseDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MakeListDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Mapper
public interface MerchantDao extends BaseDao<MerchantEntity> {
    //根据名称和身份账号查询
    MerchantEntity getByNameAndCards(@Param("name") String name, @Param("cards") String cards);
    //根据商户名查询
    MerchantEntity getByName(@Param("name") String name, @Param("status") int status);
    //根据id修改状态
    String selectOfficialMobile();
    void updateStatusById(@Param("id") long id, @Param("status") int status);
    List<MerchantDTO>  selectByMartId(Map<String, Object> params);
    List<MerchantDTO> getMerchantList(Map<String, Object> params);
    List<MerchantDTO> selectByUserlongitudeandlatitude(Map<String, Object> params);
    List<MerchantDTO> getListByOn();
    MerchantEntity getMerchantById(Long id);
    List<MerchantDTO> getMerchantByCategoryId(Map<String, Object> params);
    List<MerchantDTO> getMerchantByparty(Map<String, Object> params);
    List<MerchantDTO> getMerchantByspecial(Map<String, Object> params);
    List<MerchantDTO> getLikeMerchant(Map<String, Object> params);
    List<MerchantDTO> merchantSortingPage(Map<String, Object> params);
    List<MerchantDTO>   martLike(Map<String, Object> params);
    void  updateWX(String martId);
    //根据商户id获取商家基本信息
    MerchantDTO selectBaseInfoByMartId(long marId);
    void updateAuditById(@Param("id") long id, @Param("auditStatus") int auditStatus);
    List<MakeListDTO> selectName(Map<String, Object> params);
    Integer updateRecommend(@Param("id") Long id, @Param("recommend") Integer recommend);
    Integer updateParty(@Param("id") Long id, @Param("recommend") Integer recommend);
    Integer updateSpecial(@Param("id") Long id, @Param("recommend") Integer recommend);
    List<MerchantDTO> selectbYGoods(Map<String, Object> params);
    List<GoodDTO> selectByMidAndValue(Long merchantId,String value);
    List<GoodDTO> selectByMidAndSales(Long merchantId);
}
