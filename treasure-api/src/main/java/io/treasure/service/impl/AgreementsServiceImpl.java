package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.AgreementsDao;
import io.treasure.dto.AgreementsDTO;
import io.treasure.entity.AgreementsEntity;
import io.treasure.service.AgreementsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Service
public class AgreementsServiceImpl extends CrudServiceImpl<AgreementsDao, AgreementsEntity, AgreementsDTO> implements AgreementsService {

    @Override
    public QueryWrapper<AgreementsEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<AgreementsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}