/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.BaseService;
import io.treasure.entity.SysOssEntity;

import java.util.Map;

/**
 * 文件上传
 * 
 * @author super 63600679@qq.com
 */
public interface SysOssService extends BaseService<SysOssEntity> {

	PageData<SysOssEntity> page(Map<String, Object> params);
}
