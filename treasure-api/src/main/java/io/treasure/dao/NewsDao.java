package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.dto.NewsDto;
import io.treasure.entity.MerchantActivityEntity;
import io.treasure.entity.NewsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@Mapper
public interface NewsDao extends BaseDao<NewsEntity> {

    List<NewsDto> pageList(Map<String, Object> params);

    List<NewsDto> agreePage();

    List<NewsDto> selectByStatus(@Param("status") Integer status);

}
