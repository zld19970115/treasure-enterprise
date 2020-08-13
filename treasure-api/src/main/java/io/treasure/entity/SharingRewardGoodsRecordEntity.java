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
    private Long csrId;         //id
    private Long clientId;      //客户id
    private Integer status;     //状态
    private Integer activityId; //活动id
    private Long merchantId;    //商家id
    private Long goodsId;       //商品id
    private Integer goodsNum;   //商品数量
    private Date expireTime;    //过期时间
    private Date updatePmt;     //更新时间
}
