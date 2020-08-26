package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.common.utils.Result;
import io.treasure.dto.CardInfoDTO;
import io.treasure.entity.CardInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserCardDao  extends BaseDao<CardInfoEntity> {
    CardInfoEntity  selectByIdAndPassword(@Param("id") long id, @Param("password") String password);
    CardInfoEntity  selectByIdAndPasswordandType(@Param("id") long id, @Param("password") String password);
    List<CardInfoDTO> pageList(Map params);

    int openCard(@Param("ids") List<Long> ids,@Param("userId") Long userId);
    List<CardInfoDTO> selectByNoCode();
    void updateCode(@Param("s")String s, @Param("id")long id);
}
