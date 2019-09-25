package io.treasure.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.EvaluateDao;
import io.treasure.dto.EvaluateDTO;
import io.treasure.entity.EvaluateEntity;
import io.treasure.service.EvaluateService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 评价表
 */
@Service
public class EvaluateServiceImpl extends CrudServiceImpl<EvaluateDao, EvaluateEntity, EvaluateDTO> implements EvaluateService {
    @Override
    public QueryWrapper<EvaluateEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        //状态
        String status = (String)params.get("status");
        String merchantId=(String)params.get("merchantId");
       //商户id
        QueryWrapper<EvaluateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"mart_id",merchantId);
        return wrapper;
    }
    /**
     * 商品添加
     */
    @Override
    public void addEvaluate(EvaluateEntity evaluateEntity) {
        baseDao.addEvaluate(evaluateEntity);
    }

    @Override
    public void delEvaluate(int id) {
        baseDao.delEvaluate(id);
    }

    @Override
    public Double selectAvgSpeed(long merchantId) {
        return baseDao.selectAvgSpeed(merchantId);
    }

    @Override
    public Double selectAvgHygiene(long merchantId) {
        return baseDao.selectAvgHygiene(merchantId);
    }

    @Override
    public Double selectAvgAttitude(long merchantId) {
        return baseDao.selectAvgAttitude(merchantId);
    }

    @Override
    public Double selectAvgFlavor(long merchantId) {
        return baseDao.selectAvgFlavor(merchantId);
    }

    @Override
    public Double selectAvgAllScore(long merchantId) {
        return baseDao.selectAvgAllScore(merchantId);
    }

    @Override
    public EvaluateEntity selectByUserIdAndOid(long userId, String merchantOrderId) {
        return baseDao.selectByUserIdAndOid(userId,merchantOrderId);
    }

    @Override
    public List<EvaluateEntity> selectByMerchantId(long merchantId) {
        return baseDao.selectByMerchantId(merchantId);
    }


}
