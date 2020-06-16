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
        private Integer activityId;
        private Long initiatorId;
        private Integer proposeSequeueNo;//发起助力顺序号
        private String helperMobile;
        private Integer helperValue;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createPmt;

        //insert前校验
        public boolean notNullValidate(){
                if(this.activityId == null
                        ||this.initiatorId == null
                        ||this.proposeSequeueNo == null
                        ||this.helperMobile == null
                        ||this.helperMobile == null
                ){
                        System.out.println("SharingActivityLogDto/notNullValidate failure ...");
                        return false;
                }
                return true;
        }

}
