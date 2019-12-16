package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dao.BannerDao;
import io.treasure.entity.BannerEntity;

import java.util.List;

/**
 * 轮播图
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-13
 */
public interface BannerService extends CrudService<BannerEntity, BannerDao> {

    List<BannerEntity> getAllBanner();
}
