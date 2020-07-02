package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantResourceDao;
import io.treasure.dao.MerchantRoleResourceDao;
import io.treasure.dto.MerchantResourceDto;
import io.treasure.dto.MerchantResourceSaveDto;
import io.treasure.dto.MerchantResourceShowDto;
import io.treasure.entity.MerchantResourceEntity;
import io.treasure.service.MerchantResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MerchantResourceServiceImpl extends CrudServiceImpl<MerchantResourceDao, MerchantResourceEntity, MerchantResourceDto> implements MerchantResourceService {

    @Autowired
    private MerchantResourceDao dao;

    @Autowired
    private MerchantRoleResourceDao merchantRoleResourceDao;

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

}
