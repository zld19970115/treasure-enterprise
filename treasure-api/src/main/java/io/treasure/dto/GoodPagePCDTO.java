package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Data
public class GoodPagePCDTO {
	private Long id;
	private String name;
	private String description;
	private String barcode;
	private BigDecimal price;
	private String introduce;
	private Integer showInHot;
	private String units;
	private String remarks;
	private Long martId;
	private Integer sales;
	private Integer buyers;
	private Integer	outside;
	private BigDecimal	outPrice;
	private String createDate;
	private String icon;
	private BigDecimal favorablePrice;
	private Integer stock;
	private Integer status;
	private Integer number;
	private String merchantName;
	private String categoryName;
}