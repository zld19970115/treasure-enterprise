package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "意见反馈表")
public class OpinionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "类型 0---用户 1---商家")
    private Integer type;

    @ApiModelProperty(value = "留言板")

    private String messageBoard;

    @ApiModelProperty(value = "反馈图片")
    private String  messageImg;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "创建者 商户id 或者 用户id")
    private Long creator;

    @ApiModelProperty(value = "修改者")
    private Long updater;
}
