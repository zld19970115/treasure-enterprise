package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("appview")
public class AppviewEntity extends BaseEntity {

    @TableId
    private Long id;

    private String name;

    private String url;

}
