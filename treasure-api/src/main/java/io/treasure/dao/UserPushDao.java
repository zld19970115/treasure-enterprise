/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.UserPushEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserPushDao extends BaseDao<UserPushEntity> {

    UserPushEntity selectByCid(String clientId);

    UserPushEntity selectByUserId(Long userId);

    List pageList(Map map);

}
