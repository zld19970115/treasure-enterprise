package io.treasure.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.entity.UserWithdrawEntity;
import io.treasure.vo.PageTotalRowData;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserWithdrawService extends CrudService<UserWithdrawEntity, UserWithdrawDTO> {
   List<UserWithdrawDTO> selectByUserIdAndStasus(long UserId);
   List<UserWithdrawDTO> selectByUserIdAndalready(long UserId);
   Result audit(UserWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException;
   PageData<UserWithdrawDTO> listPage(Map<String, Object> params);

   PageTotalRowData<UserWithdrawDTO> getMerchanWithDrawByMerchantId(Map<String, Object> params);

}
