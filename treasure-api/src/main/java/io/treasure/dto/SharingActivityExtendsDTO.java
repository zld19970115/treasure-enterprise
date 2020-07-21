package io.treasure.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@ApiModel(value = "助力活动扩展表")
@Accessors(chain = true)
public class SharingActivityExtendsDTO {

    private Integer saeId;
    private Integer helperRewardType;// int(2) DEFAULT '1' COMMENT '奖励类型1-代付金，2-商品，3-菜品',
    private Integer helperRewardAmount;// int(10) NOT NULL COMMENT '奖励数量',
    private Long    helperRewardId;// bigint(20) DEFAULT NULL COMMENT '奖励商品或菜品id',
    private Integer minimumCharge;//最低消费
}
