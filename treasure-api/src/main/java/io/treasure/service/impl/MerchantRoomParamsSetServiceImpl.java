package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantRoomParamsSetDao;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.MerchantRoomParamsSetEntity;
import io.treasure.service.MerchantRoomParamsSetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
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

    @Override
    public void updateStatus(long id, int status) {
        baseDao.updateStatus(id,status);
    }

    /**
     * 查询指定日期、时间段内可用包房
     * @param useDate
     * @param roomParamsId
     * @return
     */
    @Override
    public List getAvailableRoomsByData(Date useDate, long roomParamsId,long merchantId){
        return baseDao.getAvailableRoomsByData(useDate,roomParamsId,merchantId);
    }
}