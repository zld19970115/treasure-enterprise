package io.treasure.vo;

import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SignedRewardSpecifyTimeVo {

    private SignedRewardSpecifyTimeEntity signedRewardSpecifyTimeEntity;
    private BigDecimal rewardValue;
    private String comment;


}
