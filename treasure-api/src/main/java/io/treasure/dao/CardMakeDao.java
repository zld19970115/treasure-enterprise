package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.CardMakeDTO;
import io.treasure.entity.CardMakeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 制卡表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-09-21
 */
@Mapper
public interface CardMakeDao extends BaseDao<CardMakeEntity> {

    List<CardMakeDTO> pageList(Map map);

    @Select("select IFNULL(max(card_batch),0)+1 from ct_card_make")
    int queryMax();

}