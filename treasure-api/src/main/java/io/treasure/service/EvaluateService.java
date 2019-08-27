package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.EvaluateDTO;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.EvaluateEntity;
import io.treasure.entity.GoodEntity;

import java.util.List;
import java.util.Map;

/**
 * 评价表
 */

public interface EvaluateService  extends CrudService<EvaluateEntity, EvaluateDTO> {
    /**
     * 添加评价
     */
    void addEvaluate(EvaluateEntity evaluateEntity);
    void  delEvaluate(int id);
    Double  selectAvgSpeed(long merchantId);
    Double selectAvgHygiene(long merchantId);
    Double selectAvgAttitude(long merchantId);
    Double selectAvgFlavor(long merchantId);
    Double selectAvgAllScore(long merchantId);
}
