package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.AppviewDao;
import io.treasure.entity.AppviewEntity;
import io.treasure.service.AppviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 轮播图
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-13
 */
@Service
public class AppviewServiceImpl extends CrudServiceImpl<AppviewDao, AppviewEntity, AppviewDao> implements AppviewService {

    @Autowired
    private AppviewDao dao;

    @Override
    public QueryWrapper<AppviewEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageData pageList(Map params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<AppviewEntity> page = (Page) dao.pageList(params);
        return new PageData(page.getResult(),page.getTotal());
    }
}
