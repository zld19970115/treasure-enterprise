package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.SysSmsTemplateDao;
import io.treasure.dto.SysSmsTemplateDTO;
import io.treasure.entity.SysSmsTemplateEntity;
import io.treasure.service.SysSmsTemplateService;
import io.treasure.vo.UserPushVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysSmsTemplateServiceImpl extends CrudServiceImpl<SysSmsTemplateDao, SysSmsTemplateEntity, SysSmsTemplateDTO> implements SysSmsTemplateService {

    @Autowired
    private SysSmsTemplateDao dao;

    @Override
    public QueryWrapper<SysSmsTemplateEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageData pageList(Map<String, Object> map) {
        PageHelper.startPage(Integer.parseInt(map.get("page")+""),Integer.parseInt(map.get("limit")+""));
        Page page = (Page) dao.pageList(map);
        return new PageData<>(page.getResult(),page.getTotal());
    }
}
