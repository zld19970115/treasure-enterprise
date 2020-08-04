package io.treasure.service.impl;

import io.treasure.dao.GoodDao;
import io.treasure.service.DishesMenuService;
import io.treasure.vo.SimpleDishesVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DishesMenuServiceImpl implements DishesMenuService {

    @Autowired(required = false)
    private GoodDao goodDao;

    public List<SimpleDishesVo> getList(String startLetter, int page, int num, String inList){
        List<SimpleDishesVo> simpleDishesVos = goodDao.selectDishesMenu(startLetter, page, num, inList);
        return simpleDishesVos;
    }

}
