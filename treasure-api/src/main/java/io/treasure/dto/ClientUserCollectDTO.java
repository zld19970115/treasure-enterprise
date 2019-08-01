package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
@Data
@ApiModel(value = "用户收藏")
public class ClientUserCollectDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户收藏ID")
	private Long id;

	@ApiModelProperty(value = "收藏类型：1-店铺；2-菜品")
	private Integer type;

	@ApiModelProperty(value = "店铺/菜品id，收藏项id")
	private Long collectId;

	@ApiModelProperty(value = "用户id")
	private Long clientUserId;

	@ApiModelProperty(value = "状态：0-未收藏；1-收藏；9-取消收藏")
	private Integer status;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "更新者")
	private Long updater;


}