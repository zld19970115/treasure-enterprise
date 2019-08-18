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
    //根据名称和身份账号查询
    MerchantEntity getByNameAndCards(String name, String cards);
    //根据商户名查询
    MerchantEntity getByName(String name,int status);
    //根据id修改状态
    void updateStatusById(long id,int status);

    List<MerchantEntity> getMerchantList(Map<String, Object> params);
}