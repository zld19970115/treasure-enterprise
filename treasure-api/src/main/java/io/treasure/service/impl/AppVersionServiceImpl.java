
package io.treasure.service.impl;

import io.treasure.common.utils.ConvertUtils;
import io.treasure.dao.AppVersionDao;
import io.treasure.dto.AppVersionDTO;
import io.treasure.entity.AppVersionEntity;
import io.treasure.service.AppVersionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import org.apache.commons.lang3.StringUtils;
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

    @Override
    public QueryWrapper<AppVersionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<AppVersionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public AppVersionDTO getUpdateInfo(String appId, String version) {
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

}