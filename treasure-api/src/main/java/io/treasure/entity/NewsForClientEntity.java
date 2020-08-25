package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_news_client")
public class NewsForClientEntity {

    private Long id;

    private String title; //char50

    private String content; //longtext

    private Integer status; //int 1

    private Date updateDate;

    private Date createDate;

    private Long creator;

    private Long updater;

}
