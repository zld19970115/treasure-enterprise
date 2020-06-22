package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.AppVersionDTO;
import io.treasure.entity.AppVersionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Mapper
public interface AppVersionDao extends BaseDao<AppVersionEntity> {

    String getMaxVersion(@Param("appId")String appId);

    List<AppVersionDTO> pageList(Map<String, Object> params);

}
