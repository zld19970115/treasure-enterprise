package io.treasure.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NewsEntity {

    private Long id;

    private String title;

    private String content;

    private Integer status;

    private Date updateDate;

    private Date create_date;

    private Long creator;

    private Long updater;

}
