package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_initiator")
public class SharingInitiatorEntity {

  private Long id;
  private Long initiatorId;// bigint(20) unsigned NOT NULL COMMENT 'ct_client_user/id',
  private Integer saId;// int(10) NOT NULL COMMENT '活动编号',
  private Integer rewardType;// int(2) DEFAULT '1' COMMENT '奖励类型1-代付金，2-商品，3-菜品',
  private Integer rewardValue;// int(10) DEFAULT NULL COMMENT '奖励值',
  private Integer status;// int(2) DEFAULT '0' COMMENT '0未成功，1成功',
  private Date startTime;// datetime DEFAULT NULL COMMENT '活动开始时间',
  private Date finishedTime;// datetime DEFAULT NULL COMMENT '活动截止日期',

}
