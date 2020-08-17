package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.dto.ClientUserDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.ClientUserEntity;

import java.util.List;
import java.util.Map;

public interface BusinessManagerService extends CrudService<BusinessManagerEntity, BusinessManagerDTO> {
    List<BusinessManagerDTO> getByNameAndPassWord(String realName,String passWord);
    PageData<BusinessManagerDTO>  listPage(Map<String, Object> params);
   void binding(int bmId , String  mchId);
}
