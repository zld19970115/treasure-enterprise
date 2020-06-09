package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.QueryWithdrawDto;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
@Mapper
public interface MerchantWithdrawDao extends BaseDao<MerchantWithdrawEntity> {
    void updateStatusById(long id,int status);
    BigDecimal selectTotalCath(long  martId);
    BigDecimal selectPointMoney(long  martId);
    Double selectAlreadyCash(long  martId);
    Double selectByMartId(long  martId);
    Double  selectWaitByMartId(long  martId);
    List<MerchantWithdrawEntity>  selectPoByMartID(long  martId);
    List<MasterOrderEntity>  selectOrderByMartID(Long  merchantId);
    List<MerchantWithdrawDTO> listPage(Map<String,Object> params);
    void verify(long id,long verify, int verifyState, String verifyReason, Date verifyDate);
    String selectWithStatus();
    List<MerchantWithdrawDTO>  selectByMartIdAndStasus(Long  martId);

    List<MerchantWithdrawEntity> selectByObject(QueryWithdrawDto queryWithdrawDto);
    Double selectTotalByType(QueryWithdrawDto queryWithdrawDto);

    List<MerchantWithdrawDTO> getMerchanWithDrawAll(Map<String, Object> params);
    List<MerchantWithdrawDTO> getMerchanWithDrawByMerchantId(Map<String, Object> params);
    MerchantWithdrawDTO getMerchanWithDrawByMerchantIdTotalRow(Map<String, Object> params);

}