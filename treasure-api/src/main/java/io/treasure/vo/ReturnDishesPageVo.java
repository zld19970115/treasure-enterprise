package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "退菜订单分页VO")
public class ReturnDishesPageVo {

    private String orderId;

    private String contacts;

    private String contactNumber;

    private String roomName;

    private BigDecimal payMoney;

    private String description;

    private Date eatTime;

}
