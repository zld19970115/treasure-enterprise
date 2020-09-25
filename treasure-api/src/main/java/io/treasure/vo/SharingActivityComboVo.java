package io.treasure.vo;

import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityExtendsEntity;
import lombok.Data;

@Data
public class SharingActivityComboVo {
    private SharingActivityEntity sharingActivityDto;

    private SharingActivityExtendsEntity sharingActivityExtendsEntity;
}
