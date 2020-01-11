package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.CtDaysTogetherEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/***
 * @Author: Zhangguanglin
 * @Description: 
 * @Date: 2020/1/10
 * @param null: 
 * @Return: 平台商户天合计
 */
@Mapper
public interface CtDaysTogetherDao extends BaseDao<CtDaysTogetherEntity> {
    CtDaysTogetherEntity getDateAndMerid(@Param("date") Date date, @Param("merchantId") long merchantId,@Param("type") String type);
}
