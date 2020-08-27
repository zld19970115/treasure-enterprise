package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("order_reward_withdraw_record")
public class OrderRewardWithdrawRecord {

    @TableId
    private Long id;
    private Long mId;
    private Long orderId;
    private Integer status;
    private Date createPmt;
    private Date updatePmt;

}
