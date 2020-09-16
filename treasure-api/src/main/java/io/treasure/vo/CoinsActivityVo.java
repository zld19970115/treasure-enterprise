package io.treasure.vo;

import io.treasure.entity.CoinsActivitiesEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoinsActivityVo {

    private CoinsActivitiesEntity coinsActivitiesEntity;
    private BigDecimal rewardValue;
    private String comment;
}
