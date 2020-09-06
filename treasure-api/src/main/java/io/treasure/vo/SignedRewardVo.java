package io.treasure.vo;

import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SignedRewardVo {

    private SignedRewardSpecifyTimeEntity signedRewardSpecifyTimeEntity;
    private Integer count;
    BigDecimal value;
}
