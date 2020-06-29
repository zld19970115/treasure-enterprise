package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.PrinterDao;
import io.treasure.dto.MyPrinterDto;
import io.treasure.dto.PrinterDto;
import io.treasure.entity.PrinterEntity;
import io.treasure.service.PrinterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PrinterServiceImpl extends CrudServiceImpl<PrinterDao, PrinterEntity, PrinterDto> implements PrinterService {

    @Autowired
    private PrinterDao dao;

    @Override
    public QueryWrapper<PrinterEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public Result myPrinter(Long mid) {
        MyPrinterDto dto = new MyPrinterDto();
        List<PrinterEntity> list = dao.myPrinter(mid);
        if(list.size()>0 && list.get(0) != null) {
            if(list.get(0).getState() == 0) {
                dto.setOnePrinter(list.get(0).getName());
            } else {
                dto.setTwoPrinter(list.get(0).getName());
            }
        }
        if(list.size()>1 && list.get(1) != null) {
            if(list.get(1).getState() == 0) {
                dto.setOnePrinter(list.get(1).getName());
            } else {
                dto.setTwoPrinter(list.get(1).getName());
            }
        }
        return new Result().ok(dto);
    }

    @Override
    public Result savePrinter(MyPrinterDto dto) {
        if(dto.getMid() == null) {
            return new Result().error();
        }
        dao.del(dto.getMid());
        if(dto.getOnePrinter() != null) {
            PrinterEntity info = new PrinterEntity();
            info.setMid(dto.getMid());
            info.setCreateDate(new Date());
            info.setName(dto.getOnePrinter());
            info.setState(0);
            dao.insert(info);
        }
        if(dto.getTwoPrinter() != null) {
            PrinterEntity info = new PrinterEntity();
            info.setMid(dto.getMid());
            info.setCreateDate(new Date());
            info.setName(dto.getTwoPrinter());
            info.setState(1);
            dao.insert(info);
        }
        return new Result().ok("ok");
    }

}
