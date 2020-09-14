package io.treasure.vo;

import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityExtendsEntity;
import lombok.Data;

@Data
public class SAComboForMchVo {
    private SharingActivityEntity sharingActivityEntity;
    private SharingActivityExtendsEntity sharingActivityExtendsEntity;
}
