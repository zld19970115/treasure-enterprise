package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("ct_activity")
public class ActivityEntity extends BaseEntity {

    @TableId
    private Long id;

    private String name;

    private Integer type;

    private Integer state;

    private String statrDate;

    private String endDate;

    private Date createDate;

    private Date updateDate;

    private Long creator;

    private Long updater;

}
