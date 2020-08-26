package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("order_reward_withdraw_reocrd")
public class OrderRewardWithdrawRecordEntity{


    private Long id;

    private Long mId;
    private String orderId;
    private Integer status;

    private BigDecimal totalPrice;
    private BigDecimal actualPayment;
    private BigDecimal payCoins;
    private BigDecimal platformIncome;

    private Date createPmt;
    private Date updatePmt;

}
