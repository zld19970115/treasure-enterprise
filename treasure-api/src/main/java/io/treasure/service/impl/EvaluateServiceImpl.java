package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.EvaluateDao;
import io.treasure.dto.EvaluateDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.CategoryEntity;
import io.treasure.entity.EvaluateEntity;
import io.treasure.entity.GoodEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 评价表
 */
@Service
public class EvaluateServiceImpl extends CrudServiceImpl<EvaluateDao, EvaluateEntity, EvaluateDTO> implements io.treasure.service.EvaluateService {
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




}