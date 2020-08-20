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
    private Integer rewardType;
    private Integer rewardValue;
    private String outline;
    private Integer preferenceVolume;
    private Integer method;
    private Integer cashOutStatus;
    private Integer status;
    private String auditComment;
    private Date withDrawTime;
    private Date createPmt;

    @TableLogic
    private Integer deleted;

}
