package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantClientDTO;
import io.treasure.entity.MerchantClientEntity;

import java.util.List;

/**
 * @author user
 * @title: 个推商户cid
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/1812:20
 */

public interface MerchantClientService extends CrudService<MerchantClientEntity, MerchantClientDTO> {


    void insertMerchantUserClient(String mobile, String clientId);

    List<MerchantClientDTO> getMerchantUserClientByMerchantId(Long merchantId);



}
