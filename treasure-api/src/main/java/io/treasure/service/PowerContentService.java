package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.PowerContentDTO;
import io.treasure.entity.PowerContentEntity;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力信息
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:47
 */
public interface PowerContentService extends CrudService<PowerContentEntity, PowerContentDTO> {

    /**
     * 添加助力信息
     */
    int insertPowerContent(Map<String, Object> params);

    /**
     * 根据分享人id查询助力信息
     */
    PowerContentDTO getPowerContentByUserId(Long powerlevelId);
    PowerContentEntity getPowerContentByPowerLevelId(Long powerlevelId);


}
