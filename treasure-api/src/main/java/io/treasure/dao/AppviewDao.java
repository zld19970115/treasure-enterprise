package io.treasure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.treasure.entity.AppviewEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppviewDao extends BaseMapper<AppviewEntity> {

    List<AppviewEntity> pageList(Map<String, Object> map);

}
