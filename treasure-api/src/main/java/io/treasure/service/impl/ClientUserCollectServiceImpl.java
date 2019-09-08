package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.ClientUserCollectDao;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.entity.ClientUserCollectEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.ClientUserCollectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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

    /**
     * 通过用户ID获取此用户收藏的店铺
     * @param params
     * @return
     */
    @Override
    public PageData<ClientUserCollectDTO> getCollectMerchantByUserId(Map<String, Object> params ){
        IPage<ClientUserCollectEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        List<ClientUserCollectDTO> list=baseDao.getCollectMerchantByUserId(params);
        return getPageData(list,pages.getTotal(), ClientUserCollectDTO.class);
    }

}