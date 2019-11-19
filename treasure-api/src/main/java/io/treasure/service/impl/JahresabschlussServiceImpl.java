package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.JahresabschlussDao;
import io.treasure.dto.*;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import io.treasure.service.JahresabschlussService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class JahresabschlussServiceImpl extends CrudServiceImpl<JahresabschlussDao, GoodEntity, GoodDTO> implements JahresabschlussService {
    @Override
    public QueryWrapper<GoodEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<GoodCategoryEntity> selectCategory(Map<String, Object> params) {
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        } else {
            params.put("merchantId", null);
        }
        return baseDao.selectCategory(params);
    }

    @Override
    public List<SlaveOrderDTO> selectBYgoodID(long id, String startTime1, String endTime1) {
        return baseDao.selectBYgoodID(id, startTime1, endTime1);
    }

    @Override
    public List<GoodDTO> selectByCategoeyid(long categoeyId) {
        return baseDao.selectByCategoeyid(categoeyId);
    }

    @Override
    public List<MerchantOrderDTO> selectBymerchantId(Map<String, Object> params) {
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        } else {
            params.put("merchantId", null);
        }
        return baseDao.selectBymerchantId(params);
    }

    @Override
    public List<MerchantWithdrawDTO> selectBymerchantId2(Map<String, Object> params) {
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        } else {
            params.put("merchantId", null);
        }
        return baseDao.selectBymerchantId2(params);
    }

    @Override
    public PageData<GoodCategoryDTO> selectByParams(Map<String, Object> params) {
        IPage<GoodEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        String startTime1 = (String) params.get("startTime1");//开始日期
        String endTime1 = (String) params.get("endTime1");//截止日期
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<GoodCategoryEntity> goodCategoryEntities = baseDao.selectCategory(params);//根据商户查询商户全部菜品类别
//        List<MerchantOrderDTO> merchantOrderDTOS = JahresabschlussService.selectBymerchantId(params);//根据商户查询商户所有订单
//        List<MerchantWithdrawDTO> merchantWithdrawDTO = JahresabschlussService.selectBymerchantId2(params);//根据商户查询商户所有提现
        GoodCategoryDTO goodCategoryDTO=new GoodCategoryDTO();
        for (GoodCategoryEntity entity : goodCategoryEntities) {
            List<GoodDTO> goodDTOS = baseDao.selectByCategoeyid(entity.getId());
            BigDecimal AllPayMoney = new BigDecimal("0");
            BigDecimal Alquantity = new BigDecimal("0");
            BigDecimal liyun = new BigDecimal("0.15");
            for (GoodDTO goodDTO : goodDTOS) {
                List<SlaveOrderDTO> slaveOrderDTOS = baseDao.selectBYgoodID(goodDTO.getId(), startTime1, endTime1);
                for (SlaveOrderDTO slaveOrderDTO : slaveOrderDTOS) {
                    BigDecimal payMoney = slaveOrderDTO.getPayMoney();
                    AllPayMoney = AllPayMoney.add(payMoney);
                    BigDecimal quantity = slaveOrderDTO.getQuantity();
                    Alquantity = Alquantity.add(quantity);
                }
            }
            BigDecimal multiply = AllPayMoney.multiply(liyun);
            BigDecimal subtract = AllPayMoney.subtract(multiply);
            goodCategoryDTO.setName(entity.getName());//类别名称
            goodCategoryDTO.setAlquantity(Alquantity);;//销量
            goodCategoryDTO.setAllPayMoney(AllPayMoney);//交易金额
            goodCategoryDTO.setSubtract(subtract);//可提现金额
            goodCategoryDTO.setMultiply(multiply);//平台服务费
//            a.add(Alquantity);//销量
//            a.add(AllPayMoney);//交易金额
//            a.add(subtract);//可提现金额
//            a.add(multiply);//平台服务费
        }
        return getPageData(goodCategoryEntities,pages.getTotal(), GoodCategoryDTO.class);
    }
}