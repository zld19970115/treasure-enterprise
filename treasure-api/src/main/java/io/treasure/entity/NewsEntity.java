package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_news")
public class NewsEntity {

    private Long id;

    private String title;

    private String content;

    private Integer status;

    private Date updateDate;

    private Date createDate;

    private Long creator;

    private Long updater;

    private Integer type;

    @TableField(exist = false)
    private Integer see = 0;       //0=未查看 1 =已查看
}
