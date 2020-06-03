package io.treasure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor

@ApiModel(value="外卖订单")
public class TakeoutOrdersDTO {

    @ApiModelProperty(value = "订单号")
    private String orderId;         //订单id
    @ApiModelProperty(value = "商户号")
    private Long mchId;             //商家id
    @ApiModelProperty(value = "客户电话")
    private String consumerMobile;
    @ApiModelProperty(value = "状态")
    private Integer state;              //default 0	"状态（1未支付，2已支付，3接单，4拒接单，5申请退单，6同意退单，7拒绝退单，8订单超时）"
    @ApiModelProperty(value = "状态1")
    private Integer status;             //default 0	状态位（1退款成功，2退款失败）
    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;        //default 0	未完成，1完成(代表用户已确认),2用户拒收
    @ApiModelProperty(value = "单价")
    private Integer originPrice;        //not null	原价（实时价格不保证菜与订单菜品价格一致
    @ApiModelProperty(value = "优惠券id")
    private Integer discountId;
    @ApiModelProperty(value = "优惠券面值")
    private Integer discountAmount;
    @ApiModelProperty(value = "代付金")
    private Integer giftAmount;
    @ApiModelProperty(value = "实付款")
    private Integer actualPayment;


    @ApiModelProperty(value = "支付时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @ApiModelProperty(value = "支付方式")
    private Integer paymentMethod;

    @ApiModelProperty(value = "用餐时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enjoyTime;

    @ApiModelProperty(value = "派送方式")
    private Integer shippingMethod;         //派送方式
    @ApiModelProperty(value = "派送员id")
    private String courierId;           //派送员id

    @ApiModelProperty(value = "确认收货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    @ApiModelProperty(value = "收货地址")
    private Long addressId;


    @ApiModelProperty(value = "自动：修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;            //数据库自动更新
    @ApiModelProperty(value = "0存在或1删除")
    private Integer deleted;
    @ApiModelProperty(value = "锁值")
    private Integer version;
}
