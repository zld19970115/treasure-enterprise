/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service.impl;

import io.treasure.common.service.impl.BaseServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.TokenDao;
import io.treasure.entity.TokenEntity;
import io.treasure.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service
public class TokenServiceImpl extends BaseServiceImpl<TokenDao, TokenEntity> implements TokenService {
	/**
	 * 12小时后过期
	 */
	private final static int EXPIRE = 3600 * 24 * 7;

	@Override
	public TokenEntity getByToken(String token) {
		return baseDao.getByToken(token);
	}

	@Override
	public TokenEntity createToken(Long userId) {
		//当前时间
		Date now = new Date();
		//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

		//用户token
		String token;

		//判断是否生成过token
		TokenEntity tokenEntity = baseDao.getByUserId(userId);
		if(tokenEntity == null){
			//生成一个token
			token = generateToken();

			tokenEntity = new TokenEntity();
			tokenEntity.setUserId(userId);
			tokenEntity.setToken(token);
			tokenEntity.setUpdateDate(now);
			tokenEntity.setExpireDate(expireTime);		//保存token
			this.insert(tokenEntity);
		}else{
			//判断token是否过期
			if(tokenEntity.getExpireDate().getTime() < System.currentTimeMillis()){
				//token过期，重新生成token
				token = generateToken();
			}else {
				token = tokenEntity.getToken();
			}

			tokenEntity.setToken(token);
			tokenEntity.setUpdateDate(now);
			tokenEntity.setExpireDate(expireTime);

			//更新token
			this.updateById(tokenEntity);
		}

		return tokenEntity;
	}

	@Override
	public void expireToken(Long userId){
		Date now = new Date();

		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateDate(now);
		tokenEntity.setExpireDate(now);

		this.updateById(tokenEntity);
	}

	@Override
	public TokenEntity getByUserId(Long userId) {
		return baseDao.getByUserId(userId);
	}

	private String generateToken(){
		return UUID.randomUUID().toString().replace("-", "");
	}
}
