package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.utils.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface CouponForActivityService {

    BigDecimal getClientActivityCoinsVolume(Long clientUser_id);
    BigDecimal getClientCanUseTotalCoinsVolume(Long clientUser_id);
    boolean coinsIsEnable(Long clientUser_id,BigDecimal coins);
    void updateCoinsConsumeRecord(Long clientUser_id,BigDecimal coins);
    boolean updateActivityCoinsConsumeRecord(Long clientUser_id,BigDecimal coins);
}
