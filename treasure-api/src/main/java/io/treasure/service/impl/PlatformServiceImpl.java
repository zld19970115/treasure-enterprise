package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantResourceDao;
import io.treasure.dao.PlatformDao;
import io.treasure.dto.PlatformDto;
import io.treasure.entity.PlatformEntity;
import io.treasure.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class PlatformServiceImpl extends CrudServiceImpl<PlatformDao, PlatformEntity, PlatformDto> implements PlatformService {

    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private MerchantResourceDao merchantResourceDao;

    @Override
    public QueryWrapper<PlatformEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageData<PlatformDto> pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<PlatformDto> page = (Page) platformDao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

    @Override
    public Result savePC(PlatformDto dto) {
        PlatformEntity info = new PlatformEntity();
        info.setName(dto.getName());
        info.setCreateDate(new Date());
        platformDao.insert(info);
        return new Result().ok("ok");
    }

    @Override
    public Result updatePC(PlatformDto dto) {
        PlatformEntity info = new PlatformEntity();
        info.setId(dto.getId());
        info.setName(dto.getName());
        info.setUpdateDate(new Date());
        platformDao.updateById(info);
        return new Result().ok("ok");
    }

    @Override
    public Result delPC(Long id) {
        if(merchantResourceDao.count(id) > 0) {
            return new Result().error("该平台已关联菜单不能删除?");
        }
        platformDao.deleteById(id);
        return new Result().ok("ok");
    }

}
