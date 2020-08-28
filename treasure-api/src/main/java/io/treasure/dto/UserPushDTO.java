package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.entity.CategoryEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Data
@ApiModel(value = "用户推送关系表")
public class UserPushDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	private Long id;

	private Long userId;

	private String clientId;

	private String meId;

	private String meName;

	private Date createTime;

}