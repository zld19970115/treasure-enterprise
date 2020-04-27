package io.treasure.dto;

import io.treasure.enm.EWithDrawGroupBy;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class QueryWithdrawDto extends MerchantWithdrawDTO{

    private Date startTime;
    private Date stopTime;
    private Integer groupByType;


}
