package io.treasure.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityDto {

    private Long id;

    private String name;

    private Integer type;

    private Integer state;

    private String statrDate;

    private String endDate;

    private BigDecimal totalCost;

    private Integer initNum;

}
