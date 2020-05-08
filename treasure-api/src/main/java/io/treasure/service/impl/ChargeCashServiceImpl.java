package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.dao.ChargeCashDao;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.entity.ChargeCashEntity;
import io.treasure.entity.ChargeCashSetEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.ChargeCashService;
import io.treasure.service.ChargeCashSetService;
import io.treasure.utils.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Service
public class ChargeCashServiceImpl extends CrudServiceImpl<ChargeCashDao, ChargeCashEntity, ChargeCashDTO> implements ChargeCashService {
    @Autowired
    ChargeCashSetService chargeCashSetService;
    @Override
    public QueryWrapper<ChargeCashEntity> getWrapper(Map<String, Object> params) {
        return null;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result orderSave(ChargeCashDTO dto, ClientUserEntity user) throws ParseException {
        Result result = new Result();
        //生成订单号
        String cashOrderId = OrderUtil.getCashOrderIdByTime(user.getId());
        Date d = new Date();
        //保存主订单
        ChargeCashEntity chargeCashEntity = ConvertUtils.sourceToTarget(dto, ChargeCashEntity.class);
        chargeCashEntity.setCashOrderId(cashOrderId);
        chargeCashEntity.setUserId(user.getId());
        chargeCashEntity.setCash(dto.getCash());
        ChargeCashSetEntity chargeCashSetEntity = chargeCashSetService.selectByCash(dto.getCash());
        chargeCashEntity.setChangeGift(chargeCashSetEntity.getChangeGift());
        chargeCashEntity.setCreateDate(d);
        chargeCashEntity.setSaveTime(d);
        chargeCashEntity.setStatus(1);
        int i = baseDao.insert(chargeCashEntity);
        if (i <= 0) {
            return result.error(-2, "没有订单数据！");
        }
        return result.ok(cashOrderId);
    }

    @Override
    public ChargeCashDTO selectByCashOrderId(String cashOrderId) {
        return baseDao.selectByCashOrderId(cashOrderId);
    }
}
