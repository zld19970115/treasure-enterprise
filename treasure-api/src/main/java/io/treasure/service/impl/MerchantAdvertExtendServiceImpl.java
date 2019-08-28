package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantAdvertExtendDao;
import io.treasure.dto.MerchantAdvertExtendDTO;
import io.treasure.entity.MerchantAdvertExtendEntity;
import io.treasure.service.MerchantAdvertExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商户广告位推广
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class MerchantAdvertExtendServiceImpl extends CrudServiceImpl<MerchantAdvertExtendDao, MerchantAdvertExtendEntity, MerchantAdvertExtendDTO> implements MerchantAdvertExtendService {

    @Override
    public QueryWrapper<MerchantAdvertExtendEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //商户
        String merchantId=(String)params.get("merchantId");
        //状态
        String status=(String)params.get("status");
        QueryWrapper<MerchantAdvertExtendEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        return wrapper;
    }


}