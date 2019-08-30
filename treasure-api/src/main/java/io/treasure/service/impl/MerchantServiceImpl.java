package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantDao;
import io.treasure.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Service
public class MerchantServiceImpl extends CrudServiceImpl<MerchantDao, MerchantEntity, MerchantDTO> implements MerchantService {
    /**
     * 删除
     * @param id
     */
    @Override
    public void remove(long id,int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 根据名称和身份证号查询
     * @param name
     * @param cards
     * @return
     */
    @Override
    public MerchantEntity getByNameAndCards(String name, String cards) {
        return baseDao.getByNameAndCards(name,cards);
    }

    /**
     * 商户名称查询
     * @param name
     * @param status
     * @return
     */
    @Override
    public MerchantEntity getByName(String name, int status) {
        return baseDao.getByName(name,status);
    }

    /**
     * 闭店
     * @param id
     * @param status
     */
    @Override
    public void closeShop(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    @Override
    public PageData<MerchantDTO> queryAllPage(Map<String, Object> params) {
        IPage<MerchantEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                getQueryWrapper(params)
        );

        return getPageData(page, MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> queryRoundPage(Map<String, Object> params) {
        //分页
        IPage<MerchantEntity> page = getPage(params, Constant.CREATE_DATE, false);

        //查询
        List<MerchantEntity> list = baseDao.getMerchantList(params);

        return getPageData(list, page.getTotal(), MerchantDTO.class);
    }

    @Override
    public PageData<MerchantDTO> queryPage(Map<String, Object> params) {
        IPage<MerchantEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                selectWrapper(params)
        );

        return getPageData(page, MerchantDTO.class);
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    public QueryWrapper<MerchantEntity> selectWrapper(Map<String, Object> params){
        //是否推荐
        String recommend= (String) params.get("recommend");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(recommend), "recommend", recommend);
        return wrapper;
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    public QueryWrapper<MerchantEntity> getQueryWrapper(Map<String, Object> params){
        //店铺名称
        String name= (String) params.get("name");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);
        wrapper.eq("status",1);
        return wrapper;
    }

    /**
     * 查询条件
     * @param params
     * @return
     */
    @Override
    public QueryWrapper<MerchantEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status= (String) params.get("status");
        QueryWrapper<MerchantEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.in(StringUtils.isNotBlank(status),"status",status);
        return wrapper;
    }
}