package io.treasure.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.EvaluateDao;
import io.treasure.dto.EvaluateDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.EvaluateEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantRoomEntity;
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
        wrapper.in(StringUtils.isNotBlank(merchantId),"mart_id",merchantId);
        return wrapper;
    }

    @Override
    public void addEvaluate(EvaluateEntity evaluateEntity) {
        baseDao.addEvaluate(evaluateEntity);
    }

    @Override
    public void delEvaluate(int id) {
        baseDao.delEvaluate(id);
    }

    @Override
    public Double selectAvgSpeed(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) && org.apache.commons.lang3.StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.selectAvgSpeed(params);
    }

    @Override
    public Double selectAvgHygiene(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) && org.apache.commons.lang3.StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.selectAvgHygiene(params);
    }

    @Override
    public Double selectAvgAttitude(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) && org.apache.commons.lang3.StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.selectAvgAttitude(params);
    }

    @Override
    public Double selectAvgFlavor(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) && org.apache.commons.lang3.StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.selectAvgFlavor(params);
    }

    @Override
    public Double selectAvgAllScore(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) && org.apache.commons.lang3.StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }

        return baseDao.selectAvgAllScore(params);
    }
    @Override
    public Double selectAvgAllScore2(long merchantId) {

        return baseDao.selectAvgAllScore2(merchantId);
    }
    @Override
    public EvaluateEntity selectByUserIdAndOid(long userId, String merchantOrderId) {
        return baseDao.selectByUserIdAndOid(userId,merchantOrderId);
    }

    @Override
    public List<EvaluateEntity> selectByMerchantId(long merchantId) {
        return baseDao.selectByMerchantId(merchantId);
    }

    @Override
    public PageData<EvaluateDTO> selectEvaluateDTO(Map<String, Object> params) {
        IPage<EvaluateEntity> pages=getPage(params, null,false);
        List<EvaluateDTO> list=baseDao.selectEvaluateDTO(params);
        for (EvaluateDTO dto : list) {
            MerchantEntity merchantEntity = baseDao.selectMerchantEntity(dto.getMartId());
            dto.setMerchantInfo(merchantEntity);
        }

        return getPageData(list,pages.getTotal(), EvaluateDTO.class);
    }

    @Override
    public PageData<EvaluateDTO> selectPage(Map<String, Object> params) {
        IPage<EvaluateEntity> pages=getPage(params, null,false);
        String merchantId=(String)params.get("merchantId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) && org.apache.commons.lang3.StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<EvaluateDTO> list=baseDao.selectPage1(params);
        return getPageData(list,pages.getTotal(), EvaluateDTO.class);
    }
}

