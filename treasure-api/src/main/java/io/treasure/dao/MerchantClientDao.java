package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantClientDTO;
import io.treasure.entity.MerchantClientEntity;
import io.treasure.entity.RecordGiftEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author user
 * @title: 个推商户cid
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/1811:58
 */
@Mapper
public interface MerchantClientDao extends BaseDao<MerchantClientEntity> {

    void insertMerchantUserClient(Long merchantId, String clientId);

    List<MerchantClientDTO> getMerchantUserClientByMerchantId(Long merchantId);

    List<MerchantClientDTO> getMerchantUserClientByClientId(String clientId);

    List<MerchantClientDTO> updateMenuMerchantUserClientByClientId(Long merchantId,String clientId);
}
