package io.treasure.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantResourceDao;
import io.treasure.dao.MerchantRoleDao;
import io.treasure.dao.MerchantRoleResourceDao;
import io.treasure.dao.MerchantUserRoleDao;
import io.treasure.dto.*;
import io.treasure.entity.*;
import io.treasure.service.MerchantResourceService;
import io.treasure.service.MerchantUserService;
import io.treasure.service.TokenService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantResourceServiceImpl extends CrudServiceImpl<MerchantResourceDao, MerchantResourceEntity, MerchantResourceDto> implements MerchantResourceService {

    @Autowired
    private MerchantResourceDao dao;

    @Autowired
    private MerchantRoleResourceDao merchantRoleResourceDao;

    @Autowired
    private MerchantRoleDao merchantRoleDao;

    @Autowired
    private MerchantUserRoleDao merchantUserRoleDao;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MerchantUserService merchantUserService;

    @Override
    public QueryWrapper<MerchantResourceEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MerchantResourceShowDto> menuList(Map<String, Object> params) {
        return dao.menuList(params);
    }

    @Override
    public Result menuSave(MerchantResourceSaveDto dto) {
        dto.setType(1);
        dao.add(dto);
        return new Result<>().ok("ok");
    }

    @Override
    public Result menuUpdate(MerchantResourceSaveDto dto) {
        dao.updateMenu(dto);
        return new Result<>().ok("ok");
    }

    @Override
    public Result menuDel(Long id) {
        if(dao.countMenuId(id) > 0) {
            return new Result<>().error("已被使用不能删除");
        }
        if(merchantRoleResourceDao.countByRid(id) > 0) {
            return new Result<>().error("已被使用不能删除");
        }
        dao.del(id);
        return new Result<>().ok("ok");
    }

    @Override
    public List<MerchantRoleShowDto> roleList(Map<String, Object> params) {
        return merchantRoleDao.roleList(params);
    }

    @Override
    public Result roleSave(MerchantRoleSaveDto dto) {
        MerchantRoleEntity info = new MerchantRoleEntity();
        info.setName(dto.getName());
        info.setPid(dto.getPid());
        merchantRoleDao.insert(info);
        return new Result<>().ok("ok");
    }

    @Override
    public Result roleUpdate(MerchantRoleSaveDto dto) {
        MerchantRoleEntity info = merchantRoleDao.selectById(dto.getId());
        info.setName(dto.getName());
        merchantRoleDao.updateById(info);
        return new Result<>().ok("ok");
    }

    @Override
    public Result roleDel(Long id) {
        if(merchantRoleResourceDao.countByRole(id) > 0 || merchantUserRoleDao.countByRole(id) > 0) {
            return new Result<>().error("已被使用不能删除");
        }
        merchantRoleDao.deleteById(id);
        return new Result<>().ok("ok");
    }

    @Override
    public Result roleMenuList(Map<String, Object> params) {
        params.put("menuId", 0);
        List<RoleMenuDto> list = dao.roleMenuList(params);
        if(list.size() > 0) {
            for(RoleMenuDto dto : list) {
                dto.setChecked(null);
                params.put("menuId", dto.getId());
                dto.setChildren(dao.roleMenuList(params));
            }
        }
        return new Result<>().ok(list);
    }

    @Override
    public Result saveRoleMenu(String json,Long roleId) {
        merchantRoleResourceDao.delByRole(roleId);
        if(Strings.isNotBlank(json)) {
            List<Long> list = JSONArray.parseArray(json).toJavaList(Long.class);
            if(list.size() > 0) {
                merchantRoleResourceDao.saveRoleMenu(list, roleId);
            }
        }
        return new Result<>().ok("ok");
    }

    @Override
    public Result roleUserList(Long userId) {
        List<RoleUserDto> list = merchantRoleDao.roleUserList(userId);
        return new Result<>().ok(list);
    }

    @Override
    public Result roleUserSave(Map<String, Object> params) {
        merchantUserRoleDao.userRoleDel(params);
        merchantUserRoleDao.userRoleSave(params);
        return new Result<>().ok("ok");
    }

    @Override
    public Result userInfo(String token) {
        TokenEntity t = tokenService.getByToken(token);
        MerchantUserDTO info = merchantUserService.get(t.getUserId());
        return new Result<>().ok(info);
    }

    @Override
    public Result userMenuList(String token) {
        TokenEntity t = tokenService.getByToken(token);
        List<UserMenuDto> list = dao.userMenu(t.getUserId(), 0L);
        for(UserMenuDto obj : list) {
            obj.setChildren(dao.userMenu(t.getUserId(), obj.getId()));
        }
        return new Result<>().ok(list);
    }

}
