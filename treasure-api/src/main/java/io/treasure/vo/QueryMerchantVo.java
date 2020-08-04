package io.treasure.vo;

import io.swagger.annotations.ApiImplicitParam;
import lombok.Data;

import java.util.List;

@Data
public class QueryMerchantVo {

    List<SpecifyMerchantVo> specifyMerchantVos;
    Integer totalRecords;
    Integer currentPage;
}
