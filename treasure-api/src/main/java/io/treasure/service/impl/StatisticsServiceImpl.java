package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.StatisticsDao;
import io.treasure.dto.*;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.CategoryEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.CategoryService;
import io.treasure.service.StatisticsService;
import io.treasure.utils.DateUtil;
import io.treasure.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl
        extends CrudServiceImpl<StatisticsDao, MasterOrderEntity, MasterOrderDTO> implements StatisticsService {

    @Autowired
    private CategoryService categoryService;

    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;

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
    public PageTotalRowData<MerchantAccountVo> getMerchantAccount(MerchantAccountDto dto) {
        List<String> list = Lists.newArrayList();
        list.add(DateUtil.lastMonthFormatYYYYMM(1));
        list.add(DateUtil.lastMonthFormatYYYYMM(2));
        list.add(DateUtil.lastMonthFormatYYYYMM(3));
        dto.setDateList(list);
        PageHelper.startPage(dto.getPage(),dto.getLimit());
        Page<MerchantAccountVo> page = (Page) baseDao.getMerchantAccount(dto);
        Map map = new HashMap();
        if(list != null && list.size() > 0) {
            MerchantAccountVo vo = baseDao.getMerchantAccountPageTotalRow(dto);
            if(vo != null) {
                map.put("totalCash",vo.getTotalCash());
                map.put("alreadyCash",vo.getAlreadyCash());
                map.put("notCash",vo.getNotCash());
                map.put("wartCash",vo.getWartCash());
                map.put("pointMoney",vo.getPointMoney());
                map.put("wxTotal",vo.getWxTotal());
                map.put("aliTotald",vo.getAliTotald());
                map.put("count",vo.getCount());
            }
        }
        return new PageTotalRowData<MerchantAccountVo>(page.getResult(),page.getTotal(),map);
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
    public VisualizationRoomListVo getVisualizationRoom(Map<String, Object> map) {
        VisualizationRoomListVo vo = new VisualizationRoomListVo();
        List<VisualizationRoomVo> list = baseDao.getVisualizationRoom(map);
        list.addAll(baseDao.selectRoomAllByMid(map));
        List<VisualizationRoomVo> roomList = Lists.newArrayList();
        List<VisualizationRoomVo> descList = Lists.newArrayList();
        for(VisualizationRoomVo obj : list) {
            if(obj.getType() == 1) {
                roomList.add(obj);
            } else {
                descList.add(obj);
            }
        }
        vo.setRoomList(roomList);
        vo.setDescList(descList);
        return vo;

    }

    @Override
    public PageTotalRowData<DaysTogetherPageDTO> daysTogetherPage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<DaysTogetherPageDTO> page = (Page) baseDao.daysTogetherPage(params);
        Map map = new HashMap();
        if(page.getResult() != null && page.getResult().size() > 0) {
            DaysTogetherPageDTO vo = baseDao.daysTogetherPageTotalRow(params);
            if(vo != null) {
                map.put("orderTotal",vo.getOrderTotal());
                map.put("merchantDiscountAmount",vo.getMerchantDiscountAmount());
                map.put("giftMoney",vo.getGiftMoney());
                map.put("realityMoney",vo.getRealityMoney());
                map.put("merchantProceeds",vo.getMerchantProceeds());
                map.put("platformBrokerage",vo.getPlatformBrokerage());
                map.put("serviceChanrge",vo.getServiceChanrge());
                map.put("platformRealityMoney",vo.getPlatformRealityMoney());
                map.put("payCoins",vo.getPayCoins());
                map.put("realityMoneyNew",vo.getRealityMoneyNew());
            }
        }
        return new PageTotalRowData<>(page.getResult(),page.getTotal(),map);
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
            StatSdayDetailPageVo vo = baseDao.statSdayDetailPageTotalRow(params);
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
                map.put("realityMoneyNew",vo.getRealityMoneyNew());
                map.put("yePaymoney",vo.getYePaymoney());
            }
        }
        return new PageTotalRowData<StatSdayDetailPageVo>(page.getResult(),page.getTotal(),map);
    }

    @Override
    public FmisHomeVo fmisHome(Map<String, Object> params) {
        return baseDao.fmisHome(params);
    }

    @Override
    public PageData<MerchantPageVo> merchantPage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<MerchantPageVo> page = (Page) baseDao.merchantPage(params);
        List<MerchantPageVo> list = page.getResult();
        for(MerchantPageVo vo : list) {
            if(Strings.isNotBlank(vo.getCategoryid())) {
                String[] ids = vo.getCategoryid().split(",");
                List<Long> l = new ArrayList<Long>();
                for(String str : ids) {
                    l.add(Long.parseLong(str));
                }
                String names = categoryService.getListById(l).stream().map(CategoryEntity::getName).collect(Collectors.joining(","));
                vo.setCategoryName(names);
            }
        }
        return new PageData<MerchantPageVo>(page.getResult(),page.getTotal());
    }

    @Override
    public PageData<MerchantPageVo> smsMerchantPage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<MerchantPageVo> page = (Page) baseDao.smsMerchantPage(params);
        List<MerchantPageVo> list = page.getResult();
        for(MerchantPageVo vo : list) {
            if(Strings.isNotBlank(vo.getCategoryid())) {
                String[] ids = vo.getCategoryid().split(",");
                List<Long> l = new ArrayList<Long>();
                for(String str : ids) {
                    l.add(Long.parseLong(str));
                }
                String names = categoryService.getListById(l).stream().map(CategoryEntity::getName).collect(Collectors.joining(","));
                vo.setCategoryName(names);
            }
        }
        return new PageData<MerchantPageVo>(page.getResult(),page.getTotal());
    }

    @Override
    public EChartVo userChart(Map<String, Object> params) {
        EChartVo vo = new EChartVo();
        List<String> dataCount = Lists.newArrayList();
        List<String> dateList = null;
        List<EChartInfoVo> chartInfoList = null;
        if((params.get("type")+"").equals("1")) {
            dateList = DateUtil.getDaysFormatYYYYMMDD(params.get("startDate") == null ? null : params.get("startDate")+"", params.get("endDate") == null ? null : params.get("endDate")+"");
            vo.setDataRow(dateList);
            chartInfoList = baseDao.userChartByDay(params);
        } else {
            dateList = DateUtil.getMonths(params.get("year") == null ? null : params.get("year")+"");
            vo.setDataRow(dateList);
            chartInfoList = baseDao.userChartByYear(params);
        }
        Map<String,String> map = Maps.newHashMap();
        for(EChartInfoVo chartInfoVo : chartInfoList) {
            map.put(chartInfoVo.getDataRow(), chartInfoVo.getDataCount());
        }
        for(String dateVo : dateList) {
            String obj = map.get(dateVo);
            if(obj == null) {
                dataCount.add("0");
            } else {
                dataCount.add(obj);
            }
        }
        vo.setDataCount(dataCount);
        return vo;
    }

    @Override
    public EChartVo merchantChart(Map<String, Object> params) {
        EChartVo vo = new EChartVo();
        List<String> dataCount = Lists.newArrayList();
        List<String> dateList = null;
        List<EChartInfoVo> chartInfoList = null;
        if((params.get("type")+"").equals("1")) {
            dateList = DateUtil.getDaysFormatYYYYMMDD(params.get("startDate") == null ? null : params.get("startDate")+"", params.get("endDate") == null ? null : params.get("endDate")+"");
            vo.setDataRow(dateList);
            chartInfoList = baseDao.merchantChartByDay(params);
        } else {
            dateList = DateUtil.getMonths(params.get("year") == null ? null : params.get("year")+"");
            vo.setDataRow(dateList);
            chartInfoList = baseDao.merchantChartByYear(params);
        }
        Map<String,String> map = Maps.newHashMap();
        for(EChartInfoVo chartInfoVo : chartInfoList) {
            map.put(chartInfoVo.getDataRow(), chartInfoVo.getDataCount());
        }
        for(String dateVo : dateList) {
            String obj = map.get(dateVo);
            if(obj == null) {
                dataCount.add("0");
            } else {
                dataCount.add(obj);
            }
        }
        vo.setDataCount(dataCount);
        return vo;
    }

    @Override
    public EChartOrderVo orderChart(Map<String, Object> params) {
        EChartOrderVo vo = new EChartOrderVo();
        List<String> porderCount = Lists.newArrayList();
        List<String> orderCount = Lists.newArrayList();
        List<String> dateList = null;
        List<EChartOrderInfoVo> chartInfoList = null;
        if((params.get("type")+"").equals("1")) {
            dateList = DateUtil.getDaysFormatYYYYMMDD(params.get("startDate") == null ? null : params.get("startDate")+"", params.get("endDate") == null ? null : params.get("endDate")+"");
            vo.setDataRow(dateList);
            chartInfoList = baseDao.orderChartByDay(params);
        } else {
            dateList = DateUtil.getMonths(params.get("year") == null ? null : params.get("year")+"");
            vo.setDataRow(dateList);
            chartInfoList = baseDao.orderChartByYear(params);
        }
        Map<String,String> pmap = Maps.newHashMap();
        Map<String,String> map = Maps.newHashMap();
        for(EChartOrderInfoVo chartInfoVo : chartInfoList) {
            pmap.put(chartInfoVo.getDataRow(), chartInfoVo.getPorderCount());
            map.put(chartInfoVo.getDataRow(), chartInfoVo.getOrderCount());
        }
        for(String dateVo : dateList) {
            String pobj = pmap.get(dateVo);
            String obj = map.get(dateVo);
            if(pobj == null) {
                porderCount.add("0");
            } else {
                porderCount.add(pobj);
            }
            if(obj == null) {
                orderCount.add("0");
            } else {
                orderCount.add(obj);
            }
        }
        vo.setPorderCount(porderCount);
        vo.setOrderCount(orderCount);
        return vo;
    }

    @Override
    public RealTimeOrder realTimeOrder(Map<String, Object> params) {
        return baseDao.realTimeOrder(params);
    }

    @Override
    public FmisHomeVo merchantPcHome(Map<String, Object> params) {
        FmisHomeVo vo = baseDao.fmisHome(params);
        Map<String, Object> m = new HashMap<>();
        Integer c = masterOrderDao.listMerchantPCCount(params);
        vo.setPOrderCount(c);
        return vo;
    }

    @Override
    public PointsConfigDto pointsConfigInfo() {
        return baseDao.pointsConfigInfo();
    }

    @Override
    public void updatePointsConfig(Map<String, Object> params) {
        baseDao.updatePointsConfig(params);
    }

    @Override
    public PageData selectMerPushUser(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<MerPushUserVo> page = (Page) baseDao.selectMerPushUser(params);
        return new PageData(page.getResult(),page.getTotal());
    }

}
