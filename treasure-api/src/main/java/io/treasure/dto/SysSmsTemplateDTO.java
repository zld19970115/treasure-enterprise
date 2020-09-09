package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Data
@ApiModel(value = "用户推送关系表")
public class SysSmsTemplateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	private Long id;

	private String code;

	private String name;

	private String content;

	private Integer type;

	private String remark;

	private Integer state;

	private Date time;

}