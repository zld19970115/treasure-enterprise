package io.treasure.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RewardMchList {
    private Long merchantId;
    private String salesVolume;
    private String orderVolume;
    private String merchantProceed;
    private Date eatTime;
}
