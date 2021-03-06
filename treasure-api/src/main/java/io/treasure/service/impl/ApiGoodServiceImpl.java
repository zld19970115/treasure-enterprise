package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.ApiGoodDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.GoodPagePCDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import io.treasure.service.ApiGoodService;
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
public class ApiGoodServiceImpl extends CrudServiceImpl<ApiGoodDao, GoodEntity, GoodDTO> implements ApiGoodService {


    @Override
    public QueryWrapper<GoodEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //商户Id
        String merchantId=(String)params.get("merchantId");

        QueryWrapper<GoodEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"mart_id",merchantId);
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
     * 根据商户ID 查询商户下所有菜品类别
     * @param martId
     * @return
     */
    @Override
    public  List<GoodCategoryEntity> getGoodCategoryByMartId(long martId){
        return baseDao.getGoodCategoryByMartId(martId);
    }

    /**
     * 根据商户ID 查询商户下所有菜品
     * @param
     * @return
     */
    @Override
    public List getGoodsByMartId( Map<String, Object> params){
        String martId=(String)params.get("martId");
        if (StringUtils.isNotBlank(martId) && StringUtils.isNotEmpty(martId)) {
            String[] str = martId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("martId",null);
        }

        List goodsByMartId = baseDao.getGoodsByMartId(params);
        return goodsByMartId;
    }

    @Override
    public List getoutsideGoodsByMartId(Map<String, Object> params) {
        String martId=(String)params.get("martId");
        if (StringUtils.isNotBlank(martId) && StringUtils.isNotEmpty(martId)) {
            String[] str = martId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("martId",null);
        }

        List goodsByMartId = baseDao.getoutsideGoodsByMartId(params);
        return goodsByMartId;
    }

    /**
     * 通过商户ID与菜品分类ID查询此分类的所有菜
     * @param martId
     * @param goodCategoryId
     * @return
     */
    @Override
    public List getGoodsByGoodCategoryId(long martId,long goodCategoryId){
        List goodsByGoodCategoryId = baseDao.getGoodsByGoodCategoryId(martId, goodCategoryId);
        return goodsByGoodCategoryId;
    }

    @Override
    public List getoutsideGoodsByGoodCategoryId(long martId, long goodCategoryId) {
        List goodsByGoodCategoryId = baseDao.getoutsideGoodsByGoodCategoryId(martId, goodCategoryId);
        return goodsByGoodCategoryId;
    }

    /**
     * 通过商户ID查询此商户热销菜
     * @param martId
     * @return
     */
    @Override
    public List<GoodDTO> getShowInHotbyMartId(long martId) {
        return baseDao.getShowInHotbyMartId(martId);
    }

    @Override
    public PageData<GoodPagePCDTO> goodPageListPC(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<GoodPagePCDTO> page = (Page) baseDao.goodPageListPC(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

}