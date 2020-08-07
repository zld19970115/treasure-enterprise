package io.treasure.jra;

import io.treasure.utils.MyPingyInUtil;
import io.treasure.vo.GoodsGroup;
import io.treasure.vo.SimpleDishesVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface IDishesMenuJRA {

    //取得当前所有有效集合
    List<GoodsGroup> getAllList();
    //修改菜品名称
    void update(String oldStr,String newStr);
    //删除指定菜名
    Long delOne(String dishesName);
    //添加菜品名称
    void add(String dishesName);

}
