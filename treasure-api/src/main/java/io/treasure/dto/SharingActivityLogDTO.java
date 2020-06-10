package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ApiModel(value = "助力记录表")
@Accessors(chain = true)
public class SharingActivityLogDTO {

        @ApiModelProperty(value = "助力id")
        private Long salId;
        private int activityId;
        private Long initiatorId;
        private Integer rewardAmount;
        private String helperMobile;
        private Integer helperValue;
        private Date createPmt;

}
