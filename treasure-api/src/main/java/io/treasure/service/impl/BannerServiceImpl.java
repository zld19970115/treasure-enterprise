package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.BannerDao;
import io.treasure.entity.AppVersionEntity;
import io.treasure.entity.BannerEntity;
import io.treasure.service.BannerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
}
