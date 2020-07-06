package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_and_distribution_params")
public class SharingAndDistributionParamsEntity {

    @TableId
    private Integer id = 1;
    private Integer helpedTimes = 3;
    private Integer months = 1;
    private Integer alwaysRegisterSuccess = 1;

}
