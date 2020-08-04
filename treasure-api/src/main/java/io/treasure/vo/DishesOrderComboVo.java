package io.treasure.vo;

import lombok.Data;

import java.util.List;

@Data
public class DishesOrderComboVo {

    List<GoodsGroup> result;
    List<String> indexs;

}
