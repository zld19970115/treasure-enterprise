package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Service
public class MerchantServiceImpl extends CrudServiceImpl<MerchantDao, MerchantEntity, MerchantDTO> implements MerchantService {
    /**
     * 删除
     * @param id
     */
    @Override
    public void remove(long id) {
        baseDao.remove(id);
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    @Override
    public QueryWrapper<MerchantEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status= (String) params.get("status");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        return wrapper;
    }
}