package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.GoodDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.MerchantRoomParamsEntity;
import io.treasure.service.GoodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Service
public class GoodServiceImpl extends CrudServiceImpl<GoodDao, GoodEntity, GoodDTO> implements GoodService {


    @Override
    public QueryWrapper<GoodEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //商户Id
        String merchantId=(String)params.get("merchantId");
        //商品名称
        String name=(String)params.get("name");
        QueryWrapper<GoodEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"mart_id",merchantId);
        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        return wrapper;
    }

    /**
     * 根据商户id和菜品名称查询
     * @param name
     * @param martId
     * @return
     */
    @Override
    public List getByNameAndMerchantId(String name, long martId) {
        return baseDao.getByNameAndMerchantId(name,martId);
    }

    @Override
    public PageData<GoodDTO> listPage(Map<String, Object> params) {
        IPage<GoodEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        params.put("mart_id",merchantId);
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("mart_id",null);
        }
        List<GoodDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), GoodDTO.class);
    }

    /**
     * 上架商品
     * @param id
     * @param status
     */
    @Override
    public void on(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 下架商品
     * @param id
     * @param status
     */
    @Override
    public void off(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 删除
     * @param id
     * @param status
     */
    @Override
    public void remove(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public GoodDTO getByInfo(long id) {
        return baseDao.getByInfo(id);
    }

    @Override
    public GoodEntity getByid(long id) {
        return baseDao.getByid(id);
    }
}