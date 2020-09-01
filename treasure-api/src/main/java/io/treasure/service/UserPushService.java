package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.UserPushDTO;
import io.treasure.entity.UserPushEntity;
import io.treasure.vo.UserPushVo;

import java.util.Map;

public interface UserPushService extends CrudService<UserPushEntity, UserPushDTO> {

    UserPushEntity selectByCid(String clientId);

    UserPushEntity selectByUserId(Long userId);

    PageData<UserPushVo> pageList(Map<String, Object> map);

}
