package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Mapper
public interface MerchantDao extends BaseDao<MerchantEntity> {
    void remove(long id);
    //根据名称和身份账号查询
    MerchantEntity getByNameAndCards(String name, String cards);
}