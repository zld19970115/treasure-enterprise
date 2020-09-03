package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.AdvertisementDao;
import io.treasure.dto.AdvertisementDto;
import io.treasure.entity.AdvertisementEntity;
import io.treasure.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdvertisementServiceImpl extends CrudServiceImpl<AdvertisementDao, AdvertisementEntity, AdvertisementDto> implements AdvertisementService {

    @Autowired
    private AdvertisementDao dao;

    @Override
    public QueryWrapper<AdvertisementEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageData<AdvertisementEntity> pageList(Map<String, Object> map) {
        PageHelper.startPage(Integer.parseInt(map.get("page")+""),Integer.parseInt(map.get("limit")+""));
        Page<AdvertisementEntity> page = (Page) dao.pageList(map);
        return new PageData<>(page.getResult(),page.getTotal());
    }

}
