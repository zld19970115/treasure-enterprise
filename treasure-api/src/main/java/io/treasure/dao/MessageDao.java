package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人消息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Mapper
public interface MessageDao extends BaseDao<MessageEntity> {
	
}