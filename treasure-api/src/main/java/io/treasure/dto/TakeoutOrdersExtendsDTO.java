package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;


@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor

@ApiModel(value = "外卖订单续表")
public class TakeoutOrdersExtendsDTO {

    @ApiModelProperty(value = "订单号")
    private String orderId;         //订单id
    @ApiModelProperty(value = "退单号")
    private String refundId;
    @ApiModelProperty(value = "退款时间")
    private Date refundTime;
    @ApiModelProperty(value = "用户注释")
    private String consumerComment;
    @ApiModelProperty(value = "评星")
    private int ratingStar;
    @ApiModelProperty(value = "商家收入")
    private int merchantReceivable;     //商家收入
    @ApiModelProperty(value = "平台扣点")
    private int platformRoyalty;        //平台扣点
    @ApiModelProperty(value = "自动：更新时间")
    private Date modifyTime;            //数据库自动更新
}
