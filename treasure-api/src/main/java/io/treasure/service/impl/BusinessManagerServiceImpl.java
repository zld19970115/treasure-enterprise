package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.BusinessManagerDao;
import io.treasure.dao.ClientUserDao;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.BusinessManagerService;
import io.treasure.service.ClientUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BusinessManagerServiceImpl  extends CrudServiceImpl<BusinessManagerDao, BusinessManagerEntity, BusinessManagerDTO> implements BusinessManagerService {
    @Override
    public QueryWrapper<BusinessManagerEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<BusinessManagerDTO> getByNameAndPassWord(String realName, String passWord) {

      return   baseDao.getByNameAndPassWord(realName, passWord);
    }

    @Override
    public PageData<BusinessManagerDTO> listPage(Map<String, Object> params) {
        IPage<BusinessManagerEntity> pages=getPage(params, (String) params.get("orderField"),false);
        List<BusinessManagerDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), BusinessManagerDTO.class);
    }

    @Override
    public void binding(int bmId, String mchId) {


        baseDao.binding(bmId,mchId);
    }
}
