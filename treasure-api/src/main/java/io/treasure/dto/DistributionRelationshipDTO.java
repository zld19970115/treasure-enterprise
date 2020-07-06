package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "分销记录")
public class DistributionRelationshipDTO {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private long id;


    /**
     * 主手机号
     */
    @ApiModelProperty(value = "主手机号")
    private String mobileMaster;

    /**
     * cong手机号
     */
    @ApiModelProperty(value = "cong手机号")
    private String mobileSlaver;
    /**
     * 1:有效，2：失效
     */
    @ApiModelProperty(value = "1:有效，2：失效")
    private Integer status;
    /**
     * 关联时间
     */
    @ApiModelProperty(value = "关联时间")
    private Date unionStartTime;

    /**
     * 关联失效时间
     */
    @ApiModelProperty(value = "关联失效时间")
    private Date unionExpireTime;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private Integer saId;
}
