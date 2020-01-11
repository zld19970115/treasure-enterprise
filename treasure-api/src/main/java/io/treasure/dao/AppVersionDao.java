package io.treasure.dao;
import io.treasure.entity.AppVersionEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Mapper
public interface AppVersionDao extends BaseDao<AppVersionEntity> {

    String getMaxVersion(@Param("appId")String appId);
}