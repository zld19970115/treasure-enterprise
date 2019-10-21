package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.common.page.PageData;
import io.treasure.dto.EvaluateDTO;
import io.treasure.entity.EvaluateEntity;
import io.treasure.entity.MerchantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
    List<EvaluateEntity> selectByMerchantId(long merchantId);
    Double  selectAvgSpeed(long merchantId);
    Double selectAvgHygiene(long merchantId);
    Double selectAvgAttitude(long merchantId);
    Double selectAvgFlavor(long merchantId);
    Double selectAvgAllScore(long merchantId);
    EvaluateEntity selectByUserIdAndOid(@Param("userId") long userId ,@Param("merchantOrderId")  String merchantOrderId );
    List<EvaluateDTO> selectEvaluateDTO(Map<String, Object> params);
    MerchantEntity selectMerchantEntity(long martId);

}
