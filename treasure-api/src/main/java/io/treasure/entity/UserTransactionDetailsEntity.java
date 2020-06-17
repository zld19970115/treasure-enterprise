package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("ct_user_transaction_details")
public class UserTransactionDetailsEntity {

    @TableId
    private Long id;

    private Long userId;

    private Integer payMode;

    private Integer type;

    private String mobile;

    private BigDecimal money;

    private BigDecimal balance;

    private Date createDate;

}
