package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("m_and_s")
public class OrderSimpleEntity{
	private static final long serialVersionUID = 1L;

	private String orderId;
	private Integer status;
	private Long roomId;
	private Long goodId;
	private BigDecimal payMoney;
	private Integer reservationType;
	private Date eatTime;
	private String pOrderId;
	private Date updateDate;
	private String headImg;


}