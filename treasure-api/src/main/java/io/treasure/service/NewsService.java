package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.NewsDto;
import io.treasure.entity.NewsEntity;

import java.util.List;
import java.util.Map;


public interface NewsService extends CrudService<NewsEntity, NewsDto> {

    PageData<NewsDto> agreePage(Map<String, Object> params);

    Result<NewsDto> privacyAgrre();

    Result<NewsDto> userAgrre();
    List<NewsEntity> selectByOn(int type);

}
