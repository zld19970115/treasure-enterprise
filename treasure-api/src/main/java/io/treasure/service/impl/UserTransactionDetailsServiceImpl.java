package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.UserCardDao;
import io.treasure.dao.UserTransactionDetailsDao;
import io.treasure.dto.CardInfoDTO;
import io.treasure.dto.UserTransactionDetailsDto;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.UserTransactionDetailsEntity;
import io.treasure.service.UserTransactionDetailsService;
import io.treasure.vo.PageTotalRowData;
import io.treasure.vo.StatSdayDetailPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserTransactionDetailsServiceImpl extends CrudServiceImpl<UserTransactionDetailsDao, UserTransactionDetailsEntity, UserTransactionDetailsDto> implements UserTransactionDetailsService {

    @Autowired
    private UserTransactionDetailsDao baseDao;

    @Override
    public QueryWrapper<UserTransactionDetailsEntity> getWrapper(Map<String, Object> params) {
        return null;
    }


    @Override
    public PageTotalRowData<UserTransactionDetailsDto> pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<UserTransactionDetailsDto> page = (Page) baseDao.pageList(params);
        List<UserTransactionDetailsDto> list = page.getResult();
        Map map = new HashMap();
        if(list != null && list.size() > 0) {
            UserTransactionDetailsDto vo = baseDao.pageTotalRow(params);
            if(vo != null) {
                map.put("money",vo.getMoney());
                map.put("balance",vo.getBalance());
            }
        }
        return new PageTotalRowData<>(page.getResult(),page.getTotal(),map);
    }


}
