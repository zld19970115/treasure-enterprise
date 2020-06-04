package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.ChargeCashSetDao;
import io.treasure.dto.ChargeCashSetDTO;
import io.treasure.entity.ChargeCashSetEntity;
import io.treasure.service.ChargeCashSetService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 现金充值设置表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Service
public class ChargeCashSetServiceImpl extends CrudServiceImpl<ChargeCashSetDao, ChargeCashSetEntity, ChargeCashSetDTO> implements ChargeCashSetService {


    @Override
    public QueryWrapper<ChargeCashSetEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public ChargeCashSetEntity selectByCash(BigDecimal cash) {
        return baseDao.selectByCash(cash);
    }

    @Override
    public PageData<ChargeCashSetDTO> cashSetPageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<ChargeCashSetDTO> page = (Page) baseDao.select();
        return new PageData<ChargeCashSetDTO>(page.getResult(),page.getTotal());
    }

    @Override
    public Integer cashSetUpdate(ChargeCashSetDTO dto) {
        ChargeCashSetEntity obj = baseDao.selectById(dto.getId());
        obj.setId(dto.getId());
        obj.setCash(dto.getCash());
        obj.setChangeGift(dto.getChangeGift());
        obj.setUpdateDate(new Date());
        return baseDao.updateById(obj);
    }

    @Override
    public Integer cashSetDel(Long id) {
        ChargeCashSetEntity obj = baseDao.selectById(id);
        obj.setStatus(9);
        return baseDao.updateById(obj);
    }

    @Override
    public Integer cashSetAdd(ChargeCashSetDTO dto) {
        ChargeCashSetEntity obj = new ChargeCashSetEntity();
        BeanUtils.copyProperties(dto, obj);
        obj.setType(1);
        obj.setCreateDate(new Date());
        return baseDao.insert(obj);
    }

    @Override
    public ChargeCashSetEntity cashSetById(Long id) {
        return baseDao.selectById(id);
    }

    @Override
    public List<ChargeCashSetDTO> select() {
        return baseDao.select();
    }


}
