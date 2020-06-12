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
@ApiModel(value = "助力记录表")
@Accessors(chain = true)
public class SharingActivityLogDTO {

        @ApiModelProperty(value = "助力id")
        private Long salId;
        private int activityId;
        private Long initiatorId;
        private String helperMobile;
        private Integer helperValue;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createPmt;

}
