package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.ChargeCashSetDao;
import io.treasure.dto.ChargeCashSetDTO;
import io.treasure.entity.ChargeCashSetEntity;
import io.treasure.service.ChargeCashSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 现金充值设置表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Service
public class ChargeCashSetServiceImpl extends CrudServiceImpl<ChargeCashSetDao, ChargeCashSetEntity, ChargeCashSetDTO> implements ChargeCashSetService {


    @Override
    public QueryWrapper<ChargeCashSetEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public ChargeCashSetEntity selectByCash(BigDecimal cash) {
        return baseDao.selectByCash(cash);
    }

    @Override
    public List<ChargeCashSetDTO> select() {
        return baseDao.select();
    }


}
