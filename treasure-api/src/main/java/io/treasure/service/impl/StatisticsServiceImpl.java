package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.StatisticsDao;
import io.treasure.dto.*;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.StatisticsService;
import io.treasure.utils.DateUtil;
import io.treasure.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public PageData<MerchantAccountVo> getMerchantAccount(MerchantAccountDto dto) {
        List<String> list = Lists.newArrayList();
        list.add(DateUtil.lastMonthFormatYYYYMM(1));
        list.add(DateUtil.lastMonthFormatYYYYMM(2));
        list.add(DateUtil.lastMonthFormatYYYYMM(3));
        dto.setDateList(list);
        PageHelper.startPage(dto.getPage(),dto.getLimit());
        Page<MerchantAccountVo> page = (Page) baseDao.getMerchantAccount(dto);
        return new PageData<MerchantAccountVo>(page.getResult(),page.getTotal());
    }

    @Override
    public BigDecimal getCompletaOrder(Map<String, Object> params) {
        return baseDao.getCompletaOrder(params);
    }

    @Override
    public PageData<ReturnDishesPageVo> getReturnDishesPage(Map<String,Object> map) {
        PageHelper.startPage(Integer.parseInt(map.get("page")+""),Integer.parseInt(map.get("limit")+""));
        Page<ReturnDishesPageVo> page = (Page) baseDao.getReturnDishesPage(map);
        return new PageData<ReturnDishesPageVo>(page.getResult(),page.getTotal());
    }

    @Override
    public List<VisualizationRoomVo> getVisualizationRoom(Map<String, Object> map) {
        List list = baseDao.getVisualizationRoom(map);
        list.addAll(baseDao.selectRoomAllByMid(map));
        return list;

    }

    @Override
    public PageData<DaysTogetherPageDTO> daysTogetherPage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<DaysTogetherPageDTO> page = (Page) baseDao.daysTogetherPage(params);
        return new PageData<DaysTogetherPageDTO>(page.getResult(),page.getTotal());
    }

    @Override
    public DaysTogetherStatisticsVo daysTogetherStat(Map<String, Object> params) {
        return baseDao.daysTogetherStat(params);
    }

    @Override
    public PageTotalRowData<StatSdayDetailPageVo> statSdayDetailPage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<StatSdayDetailPageVo> page = (Page) baseDao.statSdayDetailPage(params);
        List<StatSdayDetailPageVo> list = page.getResult();
        Map map = new HashMap();
        if(list != null && list.size() > 0) {
            List<Long> ids = list.stream().map(StatSdayDetailPageVo::getId).collect(Collectors.toList());
            StatSdayDetailPageVo vo = baseDao.statSdayDetailPageTotalRow(ids);
            if(vo != null) {
                map.put("orderTotal",vo.getOrderTotal());
                map.put("merchantDiscountAmount",vo.getMerchantDiscountAmount());
                map.put("transactionAmount",vo.getTransactionAmount());
                map.put("giftMoney",vo.getGiftMoney());
                map.put("realityMoney",vo.getRealityMoney());
                map.put("platformBrokerage",vo.getPlatformBrokerage());
                map.put("merchantProceeds",vo.getMerchantProceeds());
                map.put("withdrawMoney",vo.getWithdrawMoney());
                map.put("platformBalance",vo.getPlatformBalance());
                map.put("wxPaymoney",vo.getWxPaymoney());
                map.put("aliPaymoney",vo.getAliPaymoney());
                map.put("serviceCharge",vo.getServiceCharge());
            }
        }
        return new PageTotalRowData<StatSdayDetailPageVo>(page.getResult(),page.getTotal(),map);
    }

}
