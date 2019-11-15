package io.treasure.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.treasure.common.dao.BaseDao;
import io.treasure.dto.StimmeDTO;
import io.treasure.entity.StimmeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StimmeDao extends BaseDao<StimmeEntity> {


    List<StimmeEntity>   selectBymerchantId(Map<String, Object> params);

    StimmeDTO selectByOrderId(String orderId);
}
