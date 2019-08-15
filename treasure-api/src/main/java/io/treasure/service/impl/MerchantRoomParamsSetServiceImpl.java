package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantRoomParamsSetDao;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.MerchantRoomParamsSetEntity;
import io.treasure.service.MerchantRoomParamsSetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
@Service
public class MerchantRoomParamsSetServiceImpl extends CrudServiceImpl<MerchantRoomParamsSetDao, MerchantRoomParamsSetEntity, MerchantRoomParamsSetDTO> implements MerchantRoomParamsSetService {

    @Override
    public QueryWrapper<MerchantRoomParamsSetEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //商户
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<MerchantRoomParamsSetEntity> wrapper = new QueryWrapper<>();
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
    public void remove(long id,int status) {
        baseDao.updateStatus(id,status);
    }
}