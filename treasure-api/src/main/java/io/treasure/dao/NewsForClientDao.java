package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.NewsForClientDto;
import io.treasure.entity.NewsForClientEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface NewsForClientDao extends BaseDao<NewsForClientEntity> {

    List<NewsForClientDto> pageList(Map<String, Object> params);

    List<NewsForClientDto> agreePage();

    List<NewsForClientDto> selectByStatus(@Param("status") Integer status);
}
