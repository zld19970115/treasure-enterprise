package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("ct_days_together")
public class CtDaysTogetherEntity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 支付时间
     */
    private Date payDate;

    /**
     * 商户id
     */
    private long merchantId;

    /**
     * 支付方式
     */
    private String payType;


    /**
     * 订单总额
     */
    private BigDecimal orderTotal;

    /**
     * 支付平台手续费总额
     */
    private BigDecimal serviceChanrge;

    /**
     * 商户所得金额总额
     */
    private BigDecimal merchantProceeds;

    /**
     * 平台扣点金额总额
     */
    private BigDecimal platformBrokerage;





}
