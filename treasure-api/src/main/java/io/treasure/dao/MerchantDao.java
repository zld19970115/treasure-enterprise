package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;

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
    MerchantEntity getByNameAndCards(@Param("name") String name,@Param("cards") String cards);
    //根据商户名查询
    MerchantEntity getByName(@Param("name") String name, @Param("status") int status);
    //根据id修改状态
    void updateStatusById(@Param("id") long id,@Param("status") int status);

    List<MerchantDTO> getMerchantList(Map<String, Object> params);
    List<MerchantDTO> getListByOn();
    MerchantEntity getMerchantById(Long id);
}