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
    private Integer isUsed;//1表示失效,所有记录为了则表示已经汇总过了，默认为0，表示新记录

    private BigDecimal totalPrice;
    private BigDecimal actualPayment;
    private BigDecimal payCoins;
    private BigDecimal platformIncome;
    //private Integer orderVolume;

    private Date eatTime;
    private Date createPmt;
    private Date updatePmt;
    private Integer version;
    private Long msrId;

}
