package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.NewsDao;
import io.treasure.dto.NewsDto;
import io.treasure.entity.MessageEntity;
import io.treasure.entity.NewsEntity;
import io.treasure.service.NewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsDao newsDao;

    @Override
    public PageData<NewsDto> page(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<NewsDto> page = (Page) newsDao.pageList(params);
        return new PageData<NewsDto>(page.getResult(),page.getTotal());
    }

    @Override
    public List<NewsDto> list(Map<String, Object> params) {
        return null;
    }

    @Override
    public NewsDto get(Long id) {
        NewsEntity obj = newsDao.selectById(id);
        NewsDto dto = new NewsDto();
        BeanUtils.copyProperties(obj,dto);
        return dto;
    }

    @Override
    public void save(NewsDto dto) {
        NewsEntity obj = new NewsEntity();
        BeanUtils.copyProperties(dto,obj);
        obj.setCreateDate(new Date());
        newsDao.insert(obj);
    }

    @Override
    public void update(NewsDto dto) {
        NewsEntity entity = newsDao.selectById(dto.getId());
        NewsEntity obj = new NewsEntity();
        BeanUtils.copyProperties(dto,obj);
        obj.setUpdateDate(new Date());
        obj.setCreateDate(entity.getCreateDate());
        newsDao.updateById(obj);
    }

    @Override
    public void delete(Long[] ids) {
    }

    @Override
    public boolean insert(NewsEntity entity) {
        entity.setCreateDate(new Date());
        newsDao.insert(entity);
        return true;
    }

    @Override
    public boolean insertBatch(Collection<NewsEntity> entityList) {
        return false;
    }

    @Override
    public boolean insertBatch(Collection<NewsEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateById(NewsEntity entity) {
        return false;
    }

    @Override
    public boolean update(NewsEntity entity, Wrapper<NewsEntity> updateWrapper) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<NewsEntity> entityList) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<NewsEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public NewsEntity selectById(Serializable id) {
        return null;
    }

    @Override
    public boolean deleteById(Serializable id) {
        return false;
    }

    @Override
    public boolean deleteBatchIds(Collection<? extends Serializable> idList) {
        Long id = (Long) idList.toArray()[0];
        NewsEntity entity = newsDao.selectById(id);
        entity.setUpdateDate(new Date());
        entity.setStatus(9);
        newsDao.updateById(entity);
        return true;
    }

    @Override
    public PageData<NewsDto> agreePage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<NewsDto> page = (Page) newsDao.agreePage();
        return new PageData<NewsDto>(page.getResult(),page.getTotal());
    }

    @Override
    public Result<NewsDto> privacyAgrre() {
        return new Result<NewsDto>().ok(newsDao.selectByStatus(2).get(0));
    }


    @Override
    public Result<NewsDto> userAgrre() {
        return new Result<NewsDto>().ok(newsDao.selectByStatus(3).get(0));
    }
}
