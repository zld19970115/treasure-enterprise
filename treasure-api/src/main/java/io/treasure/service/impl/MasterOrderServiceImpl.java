
package io.treasure.service.impl;

import io.treasure.dao.MasterOrderDao;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.MasterOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class MasterOrderServiceImpl extends CrudServiceImpl<MasterOrderDao, MasterOrderEntity, MasterOrderDTO> implements MasterOrderService {

    @Override
    public QueryWrapper<MasterOrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}