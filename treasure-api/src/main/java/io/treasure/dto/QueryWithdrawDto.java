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
    private Integer queryType;//查询时间方式，空为精确模式，非空为以天为单位
    private Date stopTime;
    private Integer groupByType;//分组方式：

}
