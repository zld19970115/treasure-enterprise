package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.entity.ChargeCashEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Mapper
public interface ChargeCashDao  extends BaseDao<ChargeCashEntity> {
    ChargeCashDTO selectByCashOrderId(String cashOrderId);
    /***
     * 查询全部用户充值记录
     */
    List<ChargeCashDTO> getChargeCashAll(Map<String, Object> params);

    /**
     * 查询根据日期或者用户id查询充值记录
     */
    List<ChargeCashDTO> getChargeCashByCreateDate(Map<String, Object> params);

    ChargeCashDTO getChargeCashByCreateDateTotalRow(Map<String, Object> params);

}
