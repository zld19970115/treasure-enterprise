package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.entity.MerchantEntity;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "分享订单")
public class ShareOrderDTO {
    @ApiModelProperty(value = "订单id")
    private String orderId;
    @ApiModelProperty(value = "就餐时间")
    private Date dinnerTime;
    @ApiModelProperty(value = "就餐人姓名")
    private String name;
    @ApiModelProperty(value = "就餐人头像")
    private String head_photo;
    @ApiModelProperty(value = "就餐人手机号")
    private String mobile;
    @ApiModelProperty(value = "包房类型")
    private int roomType;
    @ApiModelProperty(value = "包房姓名")
    private String roomName;
    @ApiModelProperty(value = "包房人数")
    private int roomCount;
    @ApiModelProperty(value = "星期")
    private int week;
    @ApiModelProperty(value = "商户信息")
    private MerchantEntity merchantEntity;
}
