package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.annotation.ObjExtends;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_activity_extends")
public class SharingActivityExtendsEntity {

    @TableId
    private Integer saeId;
    @ObjExtends(defaultValue = "23",queryMethod = "eq")
    private Integer helperRewardType;// int(2) DEFAULT '1' COMMENT '奖励类型1-代付金，2-商品，3-菜品',
    private Integer helperRewardAmount = 0;// int(10) NOT NULL COMMENT '奖励数量',
    private Long    helperRewardId;// bigint(20) DEFAULT NULL COMMENT '奖励商品或菜品id',
    private Integer minimumCharge;//最低消费
}
