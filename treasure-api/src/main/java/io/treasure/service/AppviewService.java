package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dao.AppviewDao;
import io.treasure.entity.AppviewEntity;

import java.util.Map;

/**
 * 轮播图
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-13
 */
public interface AppviewService extends CrudService<AppviewEntity, AppviewDao> {

    PageData pageList(Map map);

}
