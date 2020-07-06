package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.*;
import io.treasure.entity.MerchantResourceEntity;

import java.util.List;
import java.util.Map;

public interface MerchantResourceService extends CrudService<MerchantResourceEntity, MerchantResourceDto> {

    List<MerchantResourceShowDto> menuList(Map<String, Object> params);

    Result menuSave(MerchantResourceSaveDto dto);

    Result menuUpdate(MerchantResourceSaveDto dto);

    Result menuDel(Long id);

    List<MerchantRoleShowDto> roleList(Map<String, Object> params);

    Result roleSave(MerchantRoleSaveDto dto);

    Result roleUpdate(MerchantRoleSaveDto dto);

    Result roleDel(Long id);

    Result roleMenuList(Map<String, Object> params);

    Result saveRoleMenu(String json,Long roleId);

    Result roleUserList(Long userId);

    Result roleUserSave(Map<String, Object> params);

    Result userInfo(String token);

    Result userMenuList(String token);

}
