package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 制卡表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-09-21
 */
@Data
@ApiModel(value = "制卡表")
public class CardMakeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "制卡ID")
	private Long id;

	@ApiModelProperty(value = "制卡数量")
	private Integer cardNum;

	@ApiModelProperty(value = "制卡面值")
	private BigDecimal cardMoney;

	@ApiModelProperty(value = "制卡批次")
	private Integer cardBatch;

	@ApiModelProperty(value = "制卡类型：1-赠送金")
	private String cardType;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private String createDate;

	@ApiModelProperty(value = "更新者")
	private Long updater;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	private String username;


}