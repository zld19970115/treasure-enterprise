package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.StatisticsDao;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.entity.EvaluateEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.StatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
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
    public int todayOrder(String format, Long merchantId) {
        return baseDao.todayOrder(format,merchantId);
    }

    @Override
    public int todayReserve(String format, Long merchantId) {
        return baseDao.todayReserve(format,merchantId);
    }

    @Override
    public int todayQuit(String format, Long merchantId) {
        return baseDao.todayQuit(format,merchantId);
    }

    @Override
    public double todayMoney(String format, Long merchantId) {
        return baseDao.todayMoney(format,merchantId);
    }

    @Override
    public int monthOrder(String month, Long merchantId) {
        return baseDao.monthOrder(month,merchantId);
    }

    @Override
    public int monthReserve(String month, Long merchantId) {
        return baseDao.monthReserve(month,merchantId);
    }

    @Override
    public int monthQuit(String month, Long merchantId) {
        return baseDao.monthQuit(month,merchantId);
    }

    @Override
    public double monthMoney(String month, Long merchantId) {
        return baseDao.monthMoney(month,merchantId);
    }

    @Override
    public double allMoney(Long merchantId) {
        return baseDao.allMoney(merchantId);
    }

    @Override
    public int assignOrder(String startTime1, String endTime1, Long merchantId) {
        return baseDao.assignOrder(startTime1,endTime1,merchantId);
    }

    @Override
    public int assignReserve(String startTime1, String endTime1, Long merchantId) {
        return baseDao.assignReserve(startTime1,endTime1,merchantId);
    }

    @Override
    public int assignQuit(String startTime1, String endTime1, Long merchantId) {
        return baseDao.assignQuit(startTime1,endTime1,merchantId);
    }

    @Override
    public double assignMoney(String startTime1, String endTime1, Long merchantId) {
        return baseDao.assignMoney(startTime1,endTime1,merchantId);
    }
}
