package io.treasure.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LevelVo {
    private Integer level;
    private Integer status = 0; //0--未领取 1--应领取 3---不能领取
    private BigDecimal blance;
    private String picture;
}
