/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.SysSmsDTO;
import io.treasure.entity.SysSmsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 短信
 *
 * @author super 63600679@qq.com
 */
@Mapper
public interface SysSmsDao extends BaseDao<SysSmsEntity> {

    List<SysSmsDTO> pageList(Map map);

}
