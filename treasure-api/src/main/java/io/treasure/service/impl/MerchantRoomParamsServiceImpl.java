package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantRoomParamsDao;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.entity.MerchantActivityEntity;
import io.treasure.entity.MerchantRoomParamsEntity;
import io.treasure.service.MerchantRoomParamsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商户端包房参数管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Service
public class MerchantRoomParamsServiceImpl extends CrudServiceImpl<MerchantRoomParamsDao, MerchantRoomParamsEntity, MerchantRoomParamsDTO> implements MerchantRoomParamsService {

    @Override
    public QueryWrapper<MerchantRoomParamsEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MerchantRoomParamsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public PageData<MerchantRoomParamsDTO> listPage(Map<String, Object> params) {
        IPage<MerchantRoomParamsEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<MerchantRoomParamsDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), MerchantRoomParamsDTO.class);
    }

    /**
     * 删除
     * @param id
     * @param status
     */
    @Override
    public void remove(Long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 根据商户编号和参数内容查询
     * @param merchantId
     * @param content
     * @return
     */
    @Override
    public List<MerchantRoomParamsEntity> getByMerchantIdAndContent(Long merchantId, String content,int status) {
        return baseDao.getByMerchantIdAndContent(merchantId,content,status);
    }

    @Override
    public List<MerchantRoomParamsEntity> getAllByStatus(int status) {
        return baseDao.getAllByStatus(status);
    }
}