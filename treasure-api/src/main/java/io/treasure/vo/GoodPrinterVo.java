package io.treasure.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodPrinterVo {

    private String name;

    private BigDecimal quantity;

    private BigDecimal totalMoney;

}
