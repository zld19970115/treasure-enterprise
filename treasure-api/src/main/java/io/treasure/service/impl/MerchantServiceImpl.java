package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;
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
     * 闭店
     * @param id
     * @param status
     */
    @Override
    public void closeShop(long id, int status) {
        baseDao.updateStatusById(id,status);
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