package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.ClientUserCollectDao;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.entity.ClientUserCollectEntity;
import io.treasure.service.ClientUserCollectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
@Service
public class ClientUserCollectServiceImpl extends CrudServiceImpl<ClientUserCollectDao, ClientUserCollectEntity, ClientUserCollectDTO> implements ClientUserCollectService {

    @Override
    public QueryWrapper<ClientUserCollectEntity> getWrapper(Map<String, Object> params){
        String client_user_id = (String)params.get("client_user_id");

        QueryWrapper<ClientUserCollectEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(client_user_id), "client_user_id", client_user_id);

        return wrapper;
    }


    @Override
    public ClientUserCollectDTO selectByUidAndMid(Long userId, Long martId) {
        return baseDao.selectByUidAndMid(userId,martId);
    }

    @Override
    public void changeStatus(Long userId, Long martId) {
        baseDao.changeStatus(userId,martId);
    }


}