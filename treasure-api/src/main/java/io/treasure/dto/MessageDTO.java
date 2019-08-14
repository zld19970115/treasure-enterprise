package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 个人消息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Data
@ApiModel(value = "个人消息")
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "消息记录id")
	private Long id;

	@ApiModelProperty(value = "业务类型：0 管理，1 商户，2 个人，3 系统，")
	private Integer type;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容描述")
	private String description;

	@ApiModelProperty(value = "已读状态：0-未读，1-已读，9-删除")
	private Integer status;

	@ApiModelProperty(value = "读取时间")
	private Date readTime;

	@ApiModelProperty(value = "内容详情、图文")
	private String context;

	@ApiModelProperty(value = "接收人")
	private Long receiver;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "更新者")
	private Long updater;


}