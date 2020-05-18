package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.NewsDto;
import io.treasure.entity.NewsEntity;
import org.springframework.stereotype.Service;


public interface NewsService extends CrudService<NewsEntity, NewsDto> {



}
