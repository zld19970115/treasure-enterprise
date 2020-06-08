package io.treasure.vo;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ActivityRewardVo {

    private int code;
    private BigDecimal reward;

}
