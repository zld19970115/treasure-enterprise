package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantRoomDao;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.MerchantRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@Service
public class MerchantRoomServiceImpl extends CrudServiceImpl<MerchantRoomDao, MerchantRoomEntity, MerchantRoomDTO> implements MerchantRoomService {

    @Override
    public QueryWrapper<MerchantRoomEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //名称
        String name=(String)params.get("name");
        //商户
        String merchantId=(String)params.get("merchantId");
        //类型
        String type=(String)params.get("type");
        QueryWrapper<MerchantRoomEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        wrapper.eq(StringUtils.isNotBlank(type),"type",type);
        return wrapper;
    }

    /**
     * 删除
     * @param id
     * @param status
     */
    @Override
    public void remove(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 根据名称和商户id查询
     * @param name
     * @param merchantId
     * @return
     */
    @Override
    public List getByNameAndMerchantId(String name, long merchantId,int type) {
        return baseDao.getByNameAndMerchantId(name,merchantId,type);
    }

    /**
     * 根据商户查询包房信息
     * @param merchantId
     * @return
     */
    @Override
    public List getByMerchantId(long merchantId,int status) {
        return baseDao.getByMerchantId(merchantId,status);
    }
}