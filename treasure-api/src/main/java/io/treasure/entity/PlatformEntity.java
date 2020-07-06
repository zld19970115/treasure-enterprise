package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ct_platform")
public class PlatformEntity {

    @TableId
    private Long id;

    private String name;

    private Date updateDate;

    private Date createDate;

    private Long creator;

    private Long updater;

}
