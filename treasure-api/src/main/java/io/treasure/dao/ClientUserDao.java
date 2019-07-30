package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ClientUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Mapper
public interface ClientUserDao extends BaseDao<ClientUserEntity> {

    ClientUserEntity getUserByMobile(String mobile);
}