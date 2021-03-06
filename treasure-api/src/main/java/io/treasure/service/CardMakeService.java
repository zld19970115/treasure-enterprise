package io.treasure.service;

import com.google.zxing.WriterException;
import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.CardMakeDTO;
import io.treasure.entity.CardMakeEntity;

import java.io.IOException;
import java.util.Map;

public interface CardMakeService extends CrudService<CardMakeEntity, CardMakeDTO> {

    PageData<CardMakeDTO> pageList(Map<String, Object> map);

    Result makeCard(CardMakeEntity dto) throws IOException, WriterException;

}
