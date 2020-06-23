package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.OpinionDao;
import io.treasure.dto.OpinionDTO;
import io.treasure.entity.OpinionEntity;
import io.treasure.service.OpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpinionServiceImpl extends CrudServiceImpl<OpinionDao, OpinionEntity, OpinionDTO> implements OpinionService {

    @Autowired
    private OpinionDao dao;

    @Override
    public QueryWrapper<OpinionEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public void insertOpinion(Map<String, Object> params) {
        baseDao.insertOpinion(params);
    }

    @Override
    public PageData<OpinionDTO> pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<OpinionDTO> page = (Page) dao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }
}
