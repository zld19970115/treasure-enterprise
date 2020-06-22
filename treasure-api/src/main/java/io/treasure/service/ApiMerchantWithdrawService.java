package io.treasure.service;

import com.alipay.api.AlipayApiException;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.entity.MerchantWithdrawEntity;


import javax.servlet.http.HttpServletRequest;


/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-29
 */
public interface ApiMerchantWithdrawService extends CrudService<MerchantWithdrawEntity, MerchantWithdrawDTO> {
    Result audit(MerchantWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException;
}