package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Data
@ApiModel(value = "APP版本号表")
public class AppVersionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "appId")
	private String appid;

	@ApiModelProperty(value = "app版本号")
	private String version;

	@ApiModelProperty(value = "更新包下载地址")
	private String url;

	@ApiModelProperty(value = "升级内容")
	private String note;

	@ApiModelProperty(value = "状态  0：无需升级   1：需要升级")
	private Integer status;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	private String createDateStr;

	@ApiModelProperty(value = "更新者")
	private Long updater;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;


}
