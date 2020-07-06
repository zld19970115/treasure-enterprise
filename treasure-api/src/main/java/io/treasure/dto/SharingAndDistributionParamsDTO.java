package io.treasure.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@ApiModel(value = "助力分享参数设置")
@Accessors(chain = true)
public class SharingAndDistributionParamsDTO {

    private Integer id;
    private Integer helpedTimes;
    private Integer months;
    private Integer alwaysRegisterSuccess;

}
