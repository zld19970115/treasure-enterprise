package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.entity.ClientUserEntity;

import java.util.Map;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
public interface ClientUserService extends CrudService<ClientUserEntity, ClientUserDTO> {

    boolean isRegister(String tel);

    ClientUserEntity getByMobile(String mobile);

    Map<String, Object> login(LoginDTO dto);

    ClientUserEntity getUserByPhone(String mobile);

    ClientUserEntity getUserByOpenId(String openId);

}