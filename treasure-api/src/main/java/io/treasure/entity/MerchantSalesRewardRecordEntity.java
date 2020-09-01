package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_sales_reward_record")
public class MerchantSalesRewardRecordEntity {
    
    private Long id;
    private Long mId;
    private Integer method;
    private Integer cashOutStatus;
    private Integer auditStatus;
    private String auditComment;
    private BigDecimal withDrawTime;
    private BigDecimal commissionVolume;

    private BigDecimal salesVolume;

    private Date startPmt;
    private Date stopPmt;

    @TableLogic
    private Integer deleted;

}
