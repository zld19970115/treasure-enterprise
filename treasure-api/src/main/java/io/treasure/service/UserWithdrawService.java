package io.treasure.service;

import com.alipay.api.AlipayApiException;
import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.entity.UserWithdrawEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserWithdrawService extends CrudService<UserWithdrawEntity, UserWithdrawDTO> {
   List<UserWithdrawDTO> selectByUserIdAndStasus(long UserId);
   List<UserWithdrawDTO> selectByUserIdAndalready(long UserId);
   Result audit(UserWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException;
   PageData<UserWithdrawDTO> listPage(Map<String, Object> params);
}
