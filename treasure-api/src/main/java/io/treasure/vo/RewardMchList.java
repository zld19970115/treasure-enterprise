package io.treasure.vo;

import lombok.Data;

@Data
public class RewardMchList {
    private Long merchantId;
    private String salesVolume;
    private String orderVolume;
    private String merchantProceed;
    private String eatTime;
}
