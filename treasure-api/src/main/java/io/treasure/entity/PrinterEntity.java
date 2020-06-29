package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ct_printer")
public class PrinterEntity {

    @TableId
    private Long id;

    private String name;

    private Integer state;

    private Long mid;

    private Date createDate;

}
