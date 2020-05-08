package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.GoodDao;
import io.treasure.dao.MerchantDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.service.GoodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import io.swagger.annotations.ApiImplicitParam;
//import io.treasure.common.constant.Constant;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Service
public class GoodServiceImpl extends CrudServiceImpl<GoodDao, GoodEntity, GoodDTO> implements GoodService {


    @Autowired(required = false)
    GoodDao goodDao;

    @Autowired(required = false)
    MerchantDao merchantDao;

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
        IPage<GoodEntity> pages=getPage(params,(String) params.get("ORDER_FIELD"),false);
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

    @Override
    public List<SlaveOrderEntity> getRefundGoods(String orderId, long goodId) {
        return baseDao.getRefundGoods(orderId,goodId);
    }

    @Override
    public PageData<GoodDTO> sortingPage(Map<String, Object> params) {
        IPage<GoodEntity> pages=getPage(params, (String) params.get("ORDER_FIELD"),false);
        List<GoodDTO> list=baseDao.sortingPage(params);
        return getPageData(list,pages.getTotal(), GoodDTO.class);
    }


    /**
     * 根据商家需求查询指定商品
     * @return
     */
    @Override
    public List<GoodDTO> listPageSimple(int page,int limit,Long merchantId) {

        //改用新的自定义的简化查询
        //Page<Object> pages = PageHelper.startPage(page,limit);     //分页插件
        List<GoodDTO> goods=goodDao.selectEnableGoodsByMerchantIdForUserOnly(merchantId,page,limit);
        //System.out.println("页码/页数"+pages.getPageNum()+"/"+pages.getPages());
        //PageInfo<GoodDTO> pageInfo = new PageInfo<>(goods);

        if(goods.size()>0){
            //检索商户信息并封装
            MerchantDTO merchantDTO = merchantDao.selectBaseInfoByMartId(merchantId);
            goods.get(0).setMerchantName(merchantDTO.getName());
            goods.get(0).setMerchantIcon(merchantDTO.getHeadurl());
        }

        return goods;
    }
}