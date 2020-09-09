package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_sms_template")
public class SysSmsTemplateEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String code;

    private String name;

    private String content;

    private Integer type;

    private String remark;

    private Integer state;

    private Date time;

}
