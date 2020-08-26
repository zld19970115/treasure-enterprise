package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.RecordGiftDao;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.entity.RecordGiftEntity;
import io.treasure.service.RecordGiftService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RecordGiftServiceImpl  extends CrudServiceImpl<RecordGiftDao, RecordGiftEntity, RecordGiftDTO> implements RecordGiftService {
    @Override
    public QueryWrapper<RecordGiftEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public void insertRecordGift(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift) {
      baseDao.insertRecordGift(userId,date,balanceGift,useGift);
    }

    @Override
    public void insertRecordBalance(long userId, Date date, BigDecimal balance, BigDecimal useBalance) {
        baseDao.insertRecordBalance(userId,date,balance,useBalance);
    }



    @Override
    public void insertRecordGift2(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift) {
        baseDao.insertRecordGift2(userId,date,balanceGift,useGift);
    }

    @Override
    public void insertRecordGift6(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift) {
        baseDao.insertRecordGift6(userId,date,balanceGift,useGift);
    }


    @Override
    public PageData<RecordGiftDTO> selectByUserId(Map<String, Object> params) {
        IPage<RecordGiftEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        List<RecordGiftDTO> list=baseDao.selectByUserId(params);
        return getPageData(list,pages.getTotal(), RecordGiftDTO.class);

    }


    @Override
    public int insertRecordGiftAdmin(Map<String,Object> params) {
        return baseDao.insertRecordGiftAdmin(params);
    }

    @Override
    public PageData<RecordGiftDTO> getAllRecordGoht(Map<String,Object> params) {
        //分页
        IPage<RecordGiftEntity> page = getPage(params, Constant.CREATE_DATE, false);

        //查询
        List<RecordGiftDTO> list = baseDao.getAllRecordGoht(params);

        return getPageData(list, page.getTotal(), RecordGiftDTO.class);
    }



    @Override
    public PageData<RecordGiftDTO> getRecordGiftByUserId(Map<String,Object> params) {
        //分页
        IPage<RecordGiftEntity> page = getPage(params, Constant.CREATE_DATE, false);

        //查询
        List<RecordGiftDTO> list = baseDao.getRecordGiftByUserId(params);

        return getPageData(list, page.getTotal(), RecordGiftDTO.class);
    }
}
