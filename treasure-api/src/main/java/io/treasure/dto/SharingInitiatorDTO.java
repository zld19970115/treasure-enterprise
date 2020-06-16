package io.treasure.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@ApiModel(value = "活动发起者记录表")
@Accessors(chain = true)
public class SharingInitiatorDTO {

  private Integer proposeId;//发起助力顺序号
  @ApiModelProperty(value = "client_user_id")
  private Long initiatorId;// bigint(20) unsigned NOT NULL COMMENT 'ct_client_user/id',
  private Integer saId;// int(10) NOT NULL COMMENT '活动编号',
  private Integer rewardType;// int(2) DEFAULT '1' COMMENT '奖励类型1-代付金，2-商品，3-菜品',
  private Integer rewardValue;// int(10) DEFAULT NULL COMMENT '奖励值',
  private Integer status;// int(2) DEFAULT '0' COMMENT '0未成功，1成功',

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;// datetime DEFAULT NULL COMMENT '活动开始时间',

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date finishedTime;// datetime DEFAULT NULL COMMENT '活动截止日期',

  //insert前校验
  public boolean notNullValidate(){
    if(this.initiatorId == null
            ||this.saId == null
            ||rewardType == null
            ||rewardValue == null
    ){
      System.out.println("SharingInitiatorDto/notNullValidate failure ...");
      return false;
    }
    return true;
  }

}
