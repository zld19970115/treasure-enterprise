package io.treasure.vo;

import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import lombok.Data;

import java.util.List;

@Data
public class MasterOrderVo{

    private List<MasterOrderCombo> masterOrderCombos;
    Long pages;
}
