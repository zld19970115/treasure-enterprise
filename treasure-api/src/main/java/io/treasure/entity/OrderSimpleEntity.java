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

	private Long id;
	private String orderId;
	private Integer status;
	private String roomId;
	private String goodId;
	private BigDecimal payMoney;
	private String reservationType;
	private String eatTime;
	private String pOrderId;
	private Date updateDate;
	private String headImg;
	private String clientid;

	private String roomName;
	private String contactNumber;

	public OrderSimpleEntity resetGoodId(){
		goodId = null;
		return this;
	}



}