package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@ApiModel(value = "活动管理表")
@Accessors(chain = true)
public class SharingActivityDTO {

      @ApiModelProperty(value = "活动编号")
      private Integer saId;//int(10) NOT NULL AUTO_INCREMENT COMMENT '活动编号',
      private String subject;// char(30) NOT NULL COMMENT '主题',
      private Integer memberHelper;// int(2) DEFAULT '0' COMMENT '活动协助者：0仅新用户，2新旧用户均可',
      private String successMsg;// char(50) DEFAULT NULL COMMENT '成功短消息',
      private Date repeatableTimes;// int(2) DEFAULT '0' COMMENT '重复参与活动次数',
      private Integer sharingMethod;// int(2) DEFAULT '1' COMMENT '1-人数方式，2-，3-',
      private Integer rewardType;// int(2) DEFAULT '1' COMMENT '奖励类型1-代付金，2-商品，3-菜品',
      private Integer helpersNum;// int(10) DEFAULT '0' COMMENT '协助者人数',
      private Integer rewardAmount;// int(10) NOT NULL COMMENT '奖励数量',
      private Long rewardId;// bigint(20) DEFAULT NULL COMMENT '奖励商品或菜品id',
      private Long rewardMchId;// bigint(20) DEFAULT NULL COMMENT '奖品来源商户id，为null表示平台发出(暂)',
      private Integer inStoreOnly;// int(2) DEFAULT '0' COMMENT '0非仅店内有效，1仅店内有效',
      private String rewardUnit;// char(5) DEFAULT NULL COMMENT '奖品单位',
      private String helperSuccess;// char(20) DEFAULT NULL COMMENT '协助成功话话',
      private String winningWords;// char(20) DEFAULT NULL COMMENT '奖品成功话束',
      private Date openDate;// datetime DEFAULT NULL COMMENT '活动开始时间',
      private Date closeDate;// datetime DEFAULT NULL COMMENT '活动截止日期',

}
