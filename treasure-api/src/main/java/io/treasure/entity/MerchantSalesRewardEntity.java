package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.treasure.common.dao.BaseDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_sales_reward")
public class MerchantSalesRewardEntity{

    private Long id;
    private Integer timeMode;//1下月一号起可返上月的佣金,2星期，下个星期可返上个星期的佣金，3，按天数模式，比如7天
    private Integer merchantType;
    private Integer judgeMethod;
    private Integer tradeNum;
    private Integer minimumSales;
    private Integer returnCommissionRate;
    private Integer returnCommissionType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatePmt;

}
