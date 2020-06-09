package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;


@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor

@TableName("takeout_orders")
public class TakeoutOrdersEntity {

    @TableId
    private String orderId;         //订单id
    private Long mchId;             //商家id
    private String consumerMobile;
    private Integer state;              //default 0	"状态（1未支付，2已支付，3接单，4拒接单，5申请退单，6同意退单，7拒绝退单，8订单超时）"
    private Integer status;             //default 0	状态位（1退款成功，2退款失败）
    private Integer orderStatus;        //default 0	未完成，1完成(代表用户已确认),2用户拒收
    private Integer originPrice;        //not null	原价（实时价格不保证菜与订单菜品价格一致
    private Integer discountId;
    private Integer discountAmount;
    private Integer giftAmount;
    private Integer shippingAmount;     //派送费用
    private Integer actualPayment;
    private Integer paymentTime;
    private Integer paymentMethod;
    private Date enjoyTime;
    private Date confirmTime;
    private Date modifyTime;            //数据库自动更新
    private Integer deleted;
    private Integer version;


}
