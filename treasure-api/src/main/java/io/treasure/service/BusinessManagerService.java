package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.dto.ClientUserDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.ClientUserEntity;

import java.util.List;

public interface BusinessManagerService extends CrudService<BusinessManagerEntity, BusinessManagerDTO> {
    List<BusinessManagerDTO> getByNameAndPassWord(String realName,String passWord);
}
