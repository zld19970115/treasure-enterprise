package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_level_status")
public class LevelStatusEntity  extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Integer leveled;
    private BigDecimal blance;
}
