/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.SysSmsTemplateDTO;
import io.treasure.entity.SysSmsTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysSmsTemplateDao extends BaseDao<SysSmsTemplateEntity> {

    List<SysSmsTemplateDTO> pageList(Map map);

}
