package io.treasure.service;

import io.treasure.dto.AppVersionDTO;
import io.treasure.entity.AppVersionEntity;

import io.treasure.common.service.CrudService;


/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
public interface AppVersionService extends CrudService<AppVersionEntity, AppVersionDTO> {

    AppVersionDTO getUpdateInfo(String appId, String version);
}