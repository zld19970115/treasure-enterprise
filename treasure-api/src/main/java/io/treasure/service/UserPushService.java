package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.UserPushDTO;
import io.treasure.entity.UserPushEntity;

public interface UserPushService extends CrudService<UserPushEntity, UserPushDTO> {

    UserPushEntity selectByCid(String clientId);

}
