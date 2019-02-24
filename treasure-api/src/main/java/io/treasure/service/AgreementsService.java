package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.AgreementsDTO;
import io.treasure.entity.AgreementsEntity;

import java.util.List;

/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
public interface AgreementsService extends CrudService<AgreementsEntity, AgreementsDTO> {
    //查询正常的协议
    List<AgreementsEntity> getAgreementsByStatusOn();
}