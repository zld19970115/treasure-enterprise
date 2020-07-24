package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_reward_goods_record")
public class SharingRewardGoodsRecordEntity {
    @TableId
    private Long csrId;
    private Long clientId;
    private Integer status;
    private Integer activityId;
    private Long merchantId;
    private Long goodsId;
    private Integer goodsNum;
    private Date expireTime;
    private Date updatePmt;
}
