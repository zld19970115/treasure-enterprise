/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service;

import io.treasure.common.service.BaseService;
import io.treasure.entity.TokenEntity;

/**
 * 用户Token
 * 
 * @author Super 63600679@qq.com
 */
public interface TokenService extends BaseService<TokenEntity> {

	TokenEntity getByToken(String token);

	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	TokenEntity createToken(Long userId);

	/**
	 * 设置token过期
	 * @param userId 用户ID
	 */
	void expireToken(Long userId);

}
