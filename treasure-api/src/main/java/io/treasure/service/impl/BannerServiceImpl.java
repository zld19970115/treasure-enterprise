package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.BannerDao;
import io.treasure.dto.BannerDto;
import io.treasure.entity.BannerEntity;
import io.treasure.service.BannerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 轮播图
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-13
 */
@Service
public class BannerServiceImpl extends CrudServiceImpl<BannerDao, BannerEntity, BannerDao> implements BannerService {
    @Override
    public QueryWrapper<BannerEntity> getWrapper(Map<String, Object> params) {
        String id = (String)params.get("id");

        QueryWrapper<BannerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public List<BannerEntity> getAllBanner() {
        return baseDao.getAllBanner();
    }

    @Override
    public BannerEntity bannerById(Map<String, Object> params) {
        return baseDao.selectById(Long.parseLong(params.get("id")+""));
    }

    @Override
    public int del(Map<String, Object> params) {
        Long[] ids = new Long[]{Long.parseLong(params.get("id")+"")};
        return baseDao.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public int update(BannerDto dto) {
        if(dto.getId() == null) {
            return 0;
        }
        BannerEntity obj = new BannerEntity();
        obj.setTypeId(dto.getTypeId());
        obj.setId(dto.getId());
        obj.setType(dto.getType());
        obj.setImgUrl(dto.getImgUrl());
        obj.setSort(dto.getSort());
        return baseDao.updateById(obj);
    }

    @Override
    public int insert(BannerDto dto) {
        BannerEntity obj = new BannerEntity();
        obj.setTypeId(dto.getTypeId());
        obj.setType(dto.getType());
        obj.setImgUrl(dto.getImgUrl());
        obj.setSort(dto.getSort());
        return baseDao.insert(obj);
    }
}
