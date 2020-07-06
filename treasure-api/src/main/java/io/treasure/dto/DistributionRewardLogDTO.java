package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "分销")
public class DistributionRewardLogDTO {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private long id;
    /**
     * consumer从手机号
     */
    @ApiModelProperty(value = "consumer从手机号")
    private String mobileSlaver;
    /**
     * reward主手机号
     */
    @ApiModelProperty(value = "reward主手机号")
    private String mobileMaster;
    /**
     * 1:宝币，2：代付金，3现金
     */
    @ApiModelProperty(value = "1:宝币，2：代付金，3现金")
    private Integer rewardType;
    /**
     * 参考总金额，单位为分
     */
    @ApiModelProperty(value = "参考总金额，单位为分")
    private Integer referencesTotal;
    /**
     * 例：80代表百分之80
     */
    @ApiModelProperty(value = "例：80代表百分之80")
    private Integer rewardRatio;
    /**
     * 奖励数量，单位为分
     */
    @ApiModelProperty(value = "奖励数量，单位为分")
    private Integer rewardAmount;
    /**
     * 消费时间
     */
    @ApiModelProperty(value = "消费时间")
    private Date consumeTime;
}
