package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.StatisticsDao;
import io.treasure.dto.ConsumptionRankingDto;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.MerchantAccountDto;
import io.treasure.dto.TopSellersRankingDto;
import io.treasure.entity.EvaluateEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.StatisticsService;
import io.treasure.utils.DateUtil;
import io.treasure.vo.ConsumptionRankingVo;
import io.treasure.vo.MerchantAccountVo;
import io.treasure.vo.TopSellersRankingVo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl
        extends CrudServiceImpl<StatisticsDao, MasterOrderEntity, MasterOrderDTO> implements StatisticsService {



    @Override
    public QueryWrapper<MasterOrderEntity> getWrapper(Map<String, Object> params) {
        String id = (String)params.get("id");

        //状态
        String status = (String)params.get("status");
        String merchantId=(String)params.get("merchantId");
        //商户id
        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"mart_id",merchantId);
        return wrapper;
    }

    @Override
    public int todayOrder( Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.todayOrder(params);
    }

    @Override
    public int todayReserve(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.todayReserve(params);
    }

    @Override
    public int todayQuit(Map<String, Object> params) {

        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }

        return baseDao.todayQuit(params);
    }

    @Override
    public double todayMoney( Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.todayMoney(params);
    }

    @Override
    public int monthOrder(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.monthOrder(params);
    }

    @Override
    public int monthReserve(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.monthReserve(params);
    }

    @Override
    public int monthQuit(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.monthQuit(params);
    }

    @Override
    public double monthMoney(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.monthMoney(params);
    }

    @Override
    public double allMoney(Long merchantId) {

        return baseDao.allMoney(merchantId);
    }

    @Override
    public int assignOrder(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.assignOrder(params);
    }

    @Override
    public int assignReserve(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.assignReserve(params);
    }

    @Override
    public int assignQuit(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.assignQuit(params);
    }

    @Override
    public double assignMoney(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.assignMoney(params);
    }

    @Override
    public List<TopSellersRankingVo> getTopSellersRanking(TopSellersRankingDto dto) {
        List<TopSellersRankingVo> list = baseDao.getTopSellersRanking(dto);
        BigDecimal totalMoney = BigDecimal.ZERO;
        for(TopSellersRankingVo vo : list) {
            totalMoney = totalMoney.add(vo.getTotal());
        }
        for(TopSellersRankingVo vo : list) {
            if(vo.getTotal().doubleValue() != 0) {
                vo.setProportion(vo.getTotal().divide(totalMoney,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
            }
        }
        return list;
    }

    @Override
    public List<ConsumptionRankingVo> getConsumptionRanking(ConsumptionRankingDto dto) {
        return baseDao.getConsumptionRanking(dto);
    }

    @Override
    public BigDecimal getTotalCash(Map<String, Object> params) {
        BigDecimal money = baseDao.getTotalCash(params);
        if(money == null) {
            return BigDecimal.ZERO;
        }
        return money;
    }

    @Override
    public List<MerchantAccountVo> getMerchantAccount(MerchantAccountDto dto) {
        List<String> list = Lists.newArrayList();
        list.add(DateUtil.lastMonthFormatYYYYMM(1));
        list.add(DateUtil.lastMonthFormatYYYYMM(2));
        list.add(DateUtil.lastMonthFormatYYYYMM(3));
        dto.setDateList(list);
        return baseDao.getMerchantAccount(dto);
    }

    @Override
    public BigDecimal getCompletaOrder(Map<String, Object> params) {
        return baseDao.getCompletaOrder(params);
    }

}
