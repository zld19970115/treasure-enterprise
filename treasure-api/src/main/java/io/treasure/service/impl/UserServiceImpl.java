/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service.impl;

import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.service.impl.BaseServiceImpl;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dao.UserDao;
import io.treasure.entity.TokenEntity;
import io.treasure.entity.UserEntity;
import io.treasure.dto.LoginDTO;
import io.treasure.service.TokenService;
import io.treasure.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserDao, UserEntity> implements UserService {
	@Autowired
	private TokenService tokenService;

	@Override
	public UserEntity getByMobile(String mobile) {
		return baseDao.getUserByMobile(mobile);
	}

	@Override
	public UserEntity getUserByUserId(Long userId) {
		return baseDao.getUserByUserId(userId);
	}

	@Override
	public Map<String, Object> login(LoginDTO dto) {
		UserEntity user = getByMobile(dto.getMobile());
		AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);

		//密码错误
		if(!user.getPassword().equals(DigestUtils.sha256Hex(dto.getPassword()))){
			throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
		}

		//获取登录token
		TokenEntity tokenEntity = tokenService.createToken(user.getId());

		Map<String, Object> map = new HashMap<>(2);
		map.put("token", tokenEntity.getToken());
		map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

		return map;
	}

}