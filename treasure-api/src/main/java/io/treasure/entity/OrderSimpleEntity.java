package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("m_and_s")
public class OrderSimpleEntity{
	private static final long serialVersionUID = 1L;

	private Long id;
	private String contacts;
	private String orderId;
	private Integer status;
	private String roomId;
	private String roomName;
	private String goodId;
	private BigDecimal payMoney;
	private String reservationType;
	private String eatTime;
	private String pOrderId;
	private Date updateDate;
	private String headImg;
	private String icon;
	private String clientid;
	private BigDecimal payCoins;
	private BigDecimal activityCoins;

	private String contactNumber;

	private String originPrice;
	public OrderSimpleEntity resetGoodId(){
		goodId = null;
		return this;
	}
	public OrderSimpleEntity resetName(){
		contacts = null;
		return this;
	}
	public OrderSimpleEntity resetRoomName(){
		roomName = null;
		return this;
	}
	public OrderSimpleEntity updateOriginPrice(){
		originPrice = new BigDecimal(originPrice).stripTrailingZeros().toPlainString();
		return this;
	}


}