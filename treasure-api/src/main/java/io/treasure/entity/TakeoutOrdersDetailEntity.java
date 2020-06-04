package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

@TableName("torder_detail")
public class TakeoutOrdersDetailEntity {


    @TableId
    private Long odId;
    private String odOrderId;
    private String odGoodsId;
    private Long odCataLogId;
    private Integer odState;        //单项状态(1：有效、0无效)
    private String goodsName;
    private String goodsIcon;
    private Integer goodsPrice;
    private Integer odGoodsQuantity;
    private Integer odItemSubTotal;
    private Date odOrderTime;

    private Date odModifyTime;

}
