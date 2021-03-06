package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.QueryWithdrawDto;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.vo.PageTotalRowData;

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
public interface MerchantWithdrawService extends CrudService<MerchantWithdrawEntity, MerchantWithdrawDTO> {
    //修改状态
    void updateStatusById(long id, int status);

    BigDecimal selectTotalCath(long martId);
    BigDecimal selectPointMoney(long martId);
    Double selectByMartId(long martId);
    Double  selectWaitByMartId(long martId);
    List<MerchantWithdrawEntity>  selectPoByMartID(long martId);
    Double selectAlreadyCash(long martId);
    PageData<MerchantWithdrawDTO> listPage(Map<String, Object> params);
    List<MasterOrderEntity> selectOrderByMartID(Long martId);
    List<MerchantWithdrawDTO>  selectByMartIdAndStasus(Long martId);
     //提现操作
    void verify(long id, long verify, int verifyStatus, String verifyReason, Date verifyDate);
    String selectWithStatus();

    //查询增补
    List<MerchantWithdrawEntity> selectByObject(QueryWithdrawDto queryWithdrawDto);
    //分组汇总
    Double selectTotalByType(QueryWithdrawDto queryWithdrawDto);

    PageData<MerchantWithdrawDTO>  getMerchanWithDrawAll(Map<String, Object> params);
    PageTotalRowData<MerchantWithdrawDTO> getMerchanWithDrawByMerchantId(Map<String, Object> params);

}