/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Token
 * 
 * @author Super 63600679@qq.com
 */
@Mapper
public interface TokenDao extends BaseDao<TokenEntity> {
    TokenEntity getByToken(String token);

    TokenEntity getByUserId(Long userId);
}
