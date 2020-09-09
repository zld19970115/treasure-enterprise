package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.SysSmsTemplateDTO;
import io.treasure.entity.SysSmsTemplateEntity;

import java.util.Map;

public interface SysSmsTemplateService extends CrudService<SysSmsTemplateEntity, SysSmsTemplateDTO> {

    PageData<SysSmsTemplateDTO> pageList(Map<String, Object> map);

}
