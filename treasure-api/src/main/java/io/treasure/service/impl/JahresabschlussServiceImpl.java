package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.GoodCategoryDao;
import io.treasure.dao.JahresabschlussDao;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import io.treasure.service.JahresabschlussService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JahresabschlussServiceImpl extends CrudServiceImpl<JahresabschlussDao, GoodEntity, GoodDTO> implements JahresabschlussService {
    @Override
    public QueryWrapper<GoodEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<GoodCategoryEntity> selectCategory(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        return baseDao.selectCategory(params);
    }

    @Override
    public List<SlaveOrderDTO> selectBYgoodID(long id,String startTime1,String endTime1 ) {
        return baseDao.selectBYgoodID(id,startTime1,endTime1);
    }

    @Override
    public List<GoodDTO> selectByCategoeyid(long categoeyId) {
        return baseDao.selectByCategoeyid(categoeyId);
    }
}
