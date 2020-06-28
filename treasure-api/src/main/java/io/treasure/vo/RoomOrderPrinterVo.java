package io.treasure.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomOrderPrinterVo {

    private String orderId;

    private String name;

    private String createDate;

    private String eatTime;

    private String description;

    private BigDecimal giftMoney;

    private BigDecimal totalMoney;

    private BigDecimal payMoney;

    private String roomName;

    private String contacts;

    private String contactNumber;

    List<GoodPrinterVo> goodList;

}
