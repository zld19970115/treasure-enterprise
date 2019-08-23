package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.EvaluateDTO;
import io.treasure.entity.EvaluateEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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


}
