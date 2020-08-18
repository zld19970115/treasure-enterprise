package io.treasure.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class RewardMchList {
    private Long merchantId;
    private String salesVolume;
    private String orderVolume;
    private String merchantProceed;
}
