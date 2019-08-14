
package io.treasure.service.impl;

import io.treasure.dao.MessageDao;
import io.treasure.dto.MessageDTO;
import io.treasure.entity.MessageEntity;
import io.treasure.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 个人消息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Service
public class MessageServiceImpl extends CrudServiceImpl<MessageDao, MessageEntity, MessageDTO> implements MessageService {

    @Override
    public QueryWrapper<MessageEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        String receiver = (String)params.get("receiver");
        QueryWrapper<MessageEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(receiver), "receiver", receiver);
        return wrapper;
    }


}