package io.treasure.vo;

import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import lombok.Data;

@Data
public class MasterOrderCombo {
    private MasterOrderEntity masterOrderEntity;
    private ClientUserEntity clientUserEntity;
}
