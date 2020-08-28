package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String audit_status;
    private Integer audit_comment;
    private Integer withDrawTime;
    private Integer salesVolume;
    private Integer commissionVolume;
    private String startPmt;
    private Date stopPmt;

    @TableLogic
    private Integer deleted;

}
