package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.AgreementsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Mapper
public interface AgreementsDao extends BaseDao<AgreementsEntity> {
    //查询正常协议
    List<AgreementsEntity> getAgreementsByStatusOn();
}