package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.CtDaysTogetherDTO;
import io.treasure.entity.CtDaysTogetherEntity;
import io.treasure.entity.StatsDayDetailEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/***
 * @Author: Zhangguanglin
 * @Description: 
 * @Date: 2020/1/10
 * @param null: 
 * @Return: 平台商户天合计
 */
public interface CtDaysTogetherService extends CrudService<CtDaysTogetherEntity, CtDaysTogetherDTO> {

    CtDaysTogetherEntity getDateAndMerid(Date date, long merchantId, String type);

    int decideInsertOrUpdate(Date date, long merchantId, String type, StatsDayDetailEntity sdde);
}
