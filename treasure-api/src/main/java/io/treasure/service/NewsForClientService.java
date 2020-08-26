package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.NewsForClientDto;
import io.treasure.entity.NewsForClientEntity;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public interface NewsForClientService {

    PageData<NewsForClientDto> page(Map<String, Object> params);

    List<NewsForClientDto> list(Map<String, Object> params);

    NewsForClientDto get(Long id);

    void save(NewsForClientDto dto);

    void update(NewsForClientDto dto);

    void delete(Long[] ids);

    boolean insert(NewsForClientEntity entity);

    boolean insertBatch(Collection<NewsForClientEntity> entityList);

    boolean insertBatch(Collection<NewsForClientEntity> entityList, int batchSize);

    boolean updateById(NewsForClientEntity entity);

    boolean update(NewsForClientEntity entity, Wrapper<NewsForClientEntity> updateWrapper);

    boolean updateBatchById(Collection<NewsForClientEntity> entityList);

    boolean updateBatchById(Collection<NewsForClientEntity> entityList, int batchSize);

     NewsForClientEntity selectById(Serializable id);

    boolean deleteById(Serializable id);

    boolean deleteBatchIds(Collection<? extends Serializable> idList);

    PageData<NewsForClientDto> agreePage(Map<String, Object> params);

    Result<NewsForClientDto> privacyAgrre();

    Result<NewsForClientDto> userAgrre();
}
