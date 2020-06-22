package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.MerchantClientDao;
import io.treasure.dto.MerchantClientDTO;
import io.treasure.entity.MerchantClientEntity;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.service.MerchantClientService;
import io.treasure.service.MerchantUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 个推商户cid
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/1812:22
 */
@Service
public class MerchantClientServiceImpl extends CrudServiceImpl<MerchantClientDao, MerchantClientEntity, MerchantClientDTO> implements MerchantClientService {

    @Resource
    MerchantUserService merchantUserService;


    @Override
    public QueryWrapper<MerchantClientEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public void insertMerchantUserClient(String mobile, String clientId) {
        MerchantUserEntity merchantUserEntity = merchantUserService.getByMobile(mobile);
        List<MerchantClientDTO> list = baseDao.getMerchantUserClientByClientId(clientId);
        if (list.size()==0){
            baseDao.insertMerchantUserClient(merchantUserEntity.getId(),clientId);
        }
    }

    @Override
    public List<MerchantClientDTO> getMerchantUserClientByMerchantId(Long merchantId) {
        return baseDao.getMerchantUserClientByMerchantId(merchantId);
    }

}
