package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.PrinterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PrinterDao extends BaseDao<PrinterEntity> {

    List<PrinterEntity> myPrinter(@Param("mid") Long mid);

    int del(@Param("mid") Long mid);

}
