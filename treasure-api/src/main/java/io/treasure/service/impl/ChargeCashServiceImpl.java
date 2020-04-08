package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.ChargeCashDao;
import io.treasure.dao.ClientUserCollectDao;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.entity.ChargeCashEntity;
import io.treasure.entity.ClientUserCollectEntity;
import io.treasure.service.ChargeCashService;
import io.treasure.service.ClientUserCollectService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Service
public class ChargeCashServiceImpl extends CrudServiceImpl<ChargeCashDao, ChargeCashEntity, ChargeCashDTO> implements ChargeCashService {
    @Override
    public QueryWrapper<ChargeCashEntity> getWrapper(Map<String, Object> params) {
        return null;
    }
}
