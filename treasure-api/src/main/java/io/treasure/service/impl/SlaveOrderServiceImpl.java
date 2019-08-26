
package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.service.SlaveOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class SlaveOrderServiceImpl extends CrudServiceImpl<SlaveOrderDao, SlaveOrderEntity, SlaveOrderDTO> implements SlaveOrderService {


    @Override
    public QueryWrapper<SlaveOrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SlaveOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<SlaveOrderEntity> selectByOrderId(String orderId) {
        List<SlaveOrderEntity> slaveOrderEntityList=baseDao.selectList(queryWrapper(orderId));
        return slaveOrderEntityList;
    }

    private QueryWrapper<SlaveOrderEntity> queryWrapper(String orderId){
        QueryWrapper<SlaveOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(orderId), "order_id", orderId);

        return wrapper;
    }
}