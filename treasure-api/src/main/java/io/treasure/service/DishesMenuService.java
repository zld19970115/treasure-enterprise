package io.treasure.service;

import io.treasure.vo.SimpleDishesVo;

import java.util.List;

public interface DishesMenuService {

    List<SimpleDishesVo> getList(String startLetter, int page, int num, String inList);
}
