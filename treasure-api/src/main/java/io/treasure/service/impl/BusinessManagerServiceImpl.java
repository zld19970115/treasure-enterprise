package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.BusinessManagerDao;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.BusinessManagerTrackRecordEntity;
import io.treasure.service.BusinessManagerService;
import io.treasure.vo.BusinessManagerPageVo;
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
    public void binding(Integer bmId, String mchId) {
        BusinessManagerTrackRecordEntity obj = baseDao.selectByMartIdAndBmid(Long.parseLong(mchId),bmId);
        if(obj != null) {
            baseDao.delLogById(obj.getPid());
        }
        if(bmId != null) {
            baseDao.binding(bmId,mchId);
        }
    }

    @Override
    public PageData<BusinessManagerPageVo> pagePC(Map<String, Object> map) {
        PageHelper.startPage(Integer.parseInt(map.get("page")+""),Integer.parseInt(map.get("limit")+""));
        Page<BusinessManagerPageVo> page = (Page) baseDao.pagePC(map);
        return new PageData<>(page.getResult(),page.getTotal());
    }
}
