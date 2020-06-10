package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantAccountDto;
import io.treasure.dto.UserTransactionDetailsDto;
import io.treasure.entity.UserTransactionDetailsEntity;
import io.treasure.vo.MerchantAccountVo;
import io.treasure.vo.PageTotalRowData;

import java.util.Map;

public interface UserTransactionDetailsService extends CrudService<UserTransactionDetailsEntity, UserTransactionDetailsDto> {

    PageTotalRowData<UserTransactionDetailsDto> pageList(Map<String,Object> map);

}
