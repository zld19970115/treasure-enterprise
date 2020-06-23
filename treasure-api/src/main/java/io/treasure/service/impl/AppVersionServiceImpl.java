
package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.dao.AppVersionDao;
import io.treasure.dto.AppVersionDTO;
import io.treasure.dto.CategoryPageDto;
import io.treasure.entity.AppVersionEntity;
import io.treasure.service.AppVersionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Service
public class AppVersionServiceImpl extends CrudServiceImpl<AppVersionDao, AppVersionEntity, AppVersionDTO> implements AppVersionService {

    @Autowired
    private AppVersionDao dao;

    @Override
    public QueryWrapper<AppVersionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<AppVersionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public AppVersionDTO getUpdateInfo(String appId) {
        String version=baseDao.getMaxVersion(appId);
        AppVersionEntity appVersionEntity=baseDao.selectOne(getUpdateInfoWrapper(appId,version));
        AppVersionDTO appVersionDTO=ConvertUtils.sourceToTarget(appVersionEntity, AppVersionDTO.class);
        return appVersionDTO;
    }

    public QueryWrapper<AppVersionEntity> getUpdateInfoWrapper(String appId,String version){

        QueryWrapper<AppVersionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(appId), "appid", appId);
        wrapper.eq(StringUtils.isNotBlank(version), "version", version);
        wrapper.eq("status", 1);
        return wrapper;
    }

    @Override
    public PageData<AppVersionDTO> pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<AppVersionDTO> page = (Page) dao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

}
