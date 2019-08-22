package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.service.MerchantWithdrawService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
@Service
public class MerchantWithdrawServiceImpl extends CrudServiceImpl<MerchantWithdrawDao, MerchantWithdrawEntity, MerchantWithdrawDTO> implements MerchantWithdrawService {

    @Override
    public QueryWrapper<MerchantWithdrawEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //审核状态
        String verifyState=(String)params.get("verifyState");

        QueryWrapper<MerchantWithdrawEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(verifyState),"verify_state",verifyState);
        return wrapper;
    }

    /**
     * 修改状态
     * @param id
     * @param status
     */
    @Override
    public void updateStatusById(long id, int status) {
        baseDao.updateStatusById(id,status);
    }
}