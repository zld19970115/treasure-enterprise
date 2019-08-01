package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantActivityDao;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.entity.MerchantActivityEntity;
import io.treasure.service.MerchantActivityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@Service
public class MerchantActivityServiceImpl extends CrudServiceImpl<MerchantActivityDao, MerchantActivityEntity, MerchantActivityDTO> implements MerchantActivityService {

    @Override
    public QueryWrapper<MerchantActivityEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //商户编号
        String merchantId=(String)params.get("merchantId");
        //状态
        String status=(String)params.get("status");
        QueryWrapper<MerchantActivityEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        return wrapper;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void remove(Long id,int status) {
        baseDao.updateStatusById(id,status);
    }
}