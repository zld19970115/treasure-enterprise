package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.PowerContentDTO;
import io.treasure.entity.PowerContentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author user
 * @title: 助力信息
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:16
 */
@Mapper
public interface PowerContentDao extends BaseDao<PowerContentEntity> {
    /**
     * 添加助力信息
     */
    int insertPowerContent(Map<String,Object> params);

    /**
     * 根据分享人id查询助力信息
     */
    PowerContentDTO getPowerContentByUserId(Long powerlevelId);


}
