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

@TableName("takeout_orders_extends")
public class TakeoutOrdersExtendsEntity {

    @TableId
    private String orderId;         //订单id
    private String refundId;
    private Date refundTime;
    private String consumerComment;
    private int ratingStar;
    private int merchantReceivable;     //商家收入
    private int platformRoyalty;        //平台扣点
    private Date modifyTime;            //数据库自动更新
}
