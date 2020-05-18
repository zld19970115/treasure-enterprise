package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dao.BannerDao;
import io.treasure.dto.BannerDto;
import io.treasure.entity.BannerEntity;

import java.util.List;
import java.util.Map;

/**
 * 轮播图
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-13
 */
public interface BannerService extends CrudService<BannerEntity, BannerDao> {

    List<BannerEntity> getAllBanner();

    BannerEntity bannerById(Map<String, Object> params);

    int del(Map<String, Object> params);

    int update(BannerDto dto);

    int insert(BannerDto dto);
}
