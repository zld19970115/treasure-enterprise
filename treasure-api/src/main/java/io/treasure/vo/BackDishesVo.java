package io.treasure.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BackDishesVo {

    private String orderId;

    private Long goodId;

    private String goodName;

    private String contacts;

    private String contactNumber;

    private String roomName;

    private BigDecimal payMoney;

    private String description;

    private String eatTime;

}
