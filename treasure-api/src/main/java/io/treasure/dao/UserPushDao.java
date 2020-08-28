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

@Mapper
public interface UserPushDao extends BaseDao<UserPushEntity> {

    UserPushEntity selectByCid(String clientId);

}
