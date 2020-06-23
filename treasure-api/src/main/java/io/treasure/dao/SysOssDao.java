/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.SysOssDto;
import io.treasure.entity.SysOssEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 文件上传
 *
 * @author super 63600679@qq.com
 */
@Mapper
public interface SysOssDao extends BaseDao<SysOssEntity> {

    List<SysOssDto> pageList(Map<String, Object> params);

}
