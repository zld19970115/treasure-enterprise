package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.NewsForClientDao;
import io.treasure.dto.NewsForClientDto;
import io.treasure.entity.NewsForClientEntity;
import io.treasure.service.NewsForClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NewsForClientServiceImpl implements NewsForClientService {

        @Autowired(required = false)
        private NewsForClientDao newsForClientDao;

        @Override
        public PageData<NewsForClientDto> page(Map<String, Object> params) {
            PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
            Page<NewsForClientDto> page = (Page) newsForClientDao.pageList(params);

            return new PageData<NewsForClientDto>(page.getRecords(),page.getTotal());
        }

        @Override
        public List<NewsForClientDto> list(Map<String, Object> params) {
            return null;
        }

        @Override
        public NewsForClientDto get(Long id) {
            NewsForClientEntity obj = newsForClientDao.selectById(id);
            NewsForClientDto dto = new NewsForClientDto();
            BeanUtils.copyProperties(obj,dto);
            return dto;
        }

        @Override
        public void save(NewsForClientDto dto) {
            NewsForClientEntity obj = new NewsForClientEntity();
            BeanUtils.copyProperties(dto,obj);
            obj.setCreateDate(new Date());
            newsForClientDao.insert(obj);
        }

        @Override
        public void update(NewsForClientDto dto) {
            NewsForClientEntity entity = newsForClientDao.selectById(dto.getId());
            NewsForClientEntity obj = new NewsForClientEntity();
            BeanUtils.copyProperties(dto,obj);
            obj.setUpdateDate(new Date());
            obj.setCreateDate(entity.getCreateDate());
            newsForClientDao.updateById(obj);
        }

        @Override
        public void delete(Long[] ids) {
        }

        @Override
        public boolean insert(NewsForClientEntity entity) {
            entity.setCreateDate(new Date());
            newsForClientDao.insert(entity);
            return true;
        }

        @Override
        public boolean insertBatch(Collection<NewsForClientEntity> entityList) {
            return false;
        }

        @Override
        public boolean insertBatch(Collection<NewsForClientEntity> entityList, int batchSize) {
            return false;
        }

        @Override
        public boolean updateById(NewsForClientEntity entity) {
            return false;
        }

        @Override
        public boolean update(NewsForClientEntity entity, Wrapper<NewsForClientEntity> updateWrapper) {
            return false;
        }

        @Override
        public boolean updateBatchById(Collection<NewsForClientEntity> entityList) {
            return false;
        }

        @Override
        public boolean updateBatchById(Collection<NewsForClientEntity> entityList, int batchSize) {
            return false;
        }

        @Override
        public NewsForClientEntity selectById(Serializable id) {
            return null;
        }

        @Override
        public boolean deleteById(Serializable id) {
            return false;
        }

        @Override
        public boolean deleteBatchIds(Collection<? extends Serializable> idList) {
            Long id = (Long) idList.toArray()[0];
            NewsForClientEntity entity = newsForClientDao.selectById(id);
            entity.setUpdateDate(new Date());
            entity.setStatus(9);
            newsForClientDao.updateById(entity);
            return true;
        }

        @Override
        public PageData<NewsForClientDto> agreePage(Map<String, Object> params) {
            PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
            List<NewsForClientDto> newsForClientDtos = newsForClientDao.agreePage();
            Page<NewsForClientDto> page = new Page<>();
            page.setRecords(newsForClientDtos);

            return new PageData<NewsForClientDto>(page.getRecords(),page.getTotal());
        }

        @Override
        public Result<NewsForClientDto> privacyAgrre() {
            return new Result<NewsForClientDto>().ok(newsForClientDao.selectByStatus(2).get(0));
        }


        @Override
        public Result<NewsForClientDto> userAgrre() {
            return new Result<NewsForClientDto>().ok(newsForClientDao.selectByStatus(3).get(0));
        }
}
