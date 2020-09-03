package io.treasure.service;

import io.treasure.entity.CouponRuleEntity;

public interface CouponRuleForActivityService {

    CouponRuleEntity getByTypeAndId(Integer subjectType, Integer subjectId);

}
