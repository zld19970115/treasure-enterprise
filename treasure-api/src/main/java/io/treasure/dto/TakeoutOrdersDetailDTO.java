package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

@ApiModel(value = "外卖订单详表")
public class TakeoutOrdersDetailDTO {

    @ApiModelProperty(value = "id")
    private Long odId;
    @ApiModelProperty(value = "订单id")
    private String odOrderId;
    @ApiModelProperty(value = "菜品id")
    private String odGoodsId;
    @ApiModelProperty(value = "分类")
    private Long odCataLogId;
    @ApiModelProperty(value = "状态")
    private Integer odState;        //单项状态(1：有效、0无效)
    @ApiModelProperty(value = "名称")
    private String goodsName;
    @ApiModelProperty(value = "图标")
    private String goodsIcon;
    @ApiModelProperty(value ="图标")
    private Integer goodsPrice;
    @ApiModelProperty(value = "数量")
    private Integer odGoodsQuantity;
    @ApiModelProperty(value = "小计")
    private Integer odItemSubTotal;

    @ApiModelProperty(value = "下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date odOrderTime;

    @ApiModelProperty(value = "自动：修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date odModifyTime;

}
