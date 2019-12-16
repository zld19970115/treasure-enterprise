package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.BannerEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 轮播图Banner表
 * @Author: Zhangguanglin
 * @Description:
 * @Date: 2019/12/13
 * @Return:
 */
@Mapper
public interface BannerDao extends BaseDao<BannerEntity> {

    List<BannerEntity>getAllBanner();
}
