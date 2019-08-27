package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.EvaluateEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价表
 */
@Mapper
public interface EvaluateDao extends BaseDao<EvaluateEntity> {
    /**
     * 添加评价
     */
    void addEvaluate(EvaluateEntity evaluateEntity);
    /**
     * 删除评价
     */
    void delEvaluate(int id);

    Double  selectAvgSpeed(long merchantId);
    Double selectAvgHygiene(long merchantId);
    Double selectAvgAttitude(long merchantId);
    Double selectAvgFlavor(long merchantId);
    Double selectAvgAllScore(long merchantId);
}
