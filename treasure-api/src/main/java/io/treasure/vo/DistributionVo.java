package io.treasure.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionVo {

    String contactNumber;
    BigDecimal payMoney = new BigDecimal("0");

    public DistributionVo addTotalMoney(BigDecimal amount){
        payMoney = payMoney.add(amount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        return this;
    }
}
