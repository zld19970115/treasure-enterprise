package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserTransactionDetailsDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Integer payMode;

    private Integer type;

    private String mobile;

    private BigDecimal money;

    private BigDecimal balance;

    private String createDate;

}
