package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 评价表
 * 2019.8.21
 */
@Data
@ApiModel(value = "评价表")
public class EvaluateDTO implements Serializable {

    /**
     * 评价ID
     */
    @ApiModelProperty(value = "评价ID")
    private long id;

    /**
     *环境卫生
     */
    @ApiModelProperty(value = "环境卫生")
    private Integer hygiene;
    /**
     *服务态度
     */
    @ApiModelProperty(value = "服务态度")
    private Integer attitude;
    /**
     *菜品口味
     */
    @ApiModelProperty(value = "菜品口味")
    private Integer flavor;
    /**
     *用餐价格
     */
    @ApiModelProperty(value = "用餐价格")
    private Integer price;
    /**
     *上菜速度
     */
    @ApiModelProperty(value = "上菜速度")
    private Integer speed;
    /**
     *意见建议
     */
    @ApiModelProperty(value = "意见建议")
    private String proposal;
    /**
     *用户id
     */
    @ApiModelProperty(value = "用户id")
    private long uid;
    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    @NotNull(message = "商户不能为空",groups= AddGroup.class)
    @NotNull(message = "商户不能为空",groups= UpdateGroup.class)
    private long martId;
    /**
     * 总评分
     */
    @ApiModelProperty(value = "总评分")
    private Integer totalScore;

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id")
    @NotNull(message = "商户不能为空",groups= AddGroup.class)
    @NotNull(message = "商户不能为空",groups= UpdateGroup.class)
    private long masterorderId;
    /**
     * 状态 0=冻结,1=正常
     */
    @ApiModelProperty(value = "状态 9-删除，1-启用，0-禁用")
    @NotNull( message = "状态不能为空",groups=AddGroup.class)
    @NotNull(message="状态不能为空",groups=UpdateGroup.class)
    private Integer status;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @NotNull(message = "更新者不能为空",groups = AddGroup.class)
    private long creator;
    /**
     * 修改者
     */
    @ApiModelProperty(value = "更新者")
    @NotNull(message = "更新者不能为空",groups = UpdateGroup.class)
    private long updater;
}
