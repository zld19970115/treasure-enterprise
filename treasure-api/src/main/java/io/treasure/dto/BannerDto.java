package io.treasure.dto;

import lombok.Data;

@Data
public class BannerDto {

    private Long id;

    /**
     * 位置排序
     */
    private Integer sort;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 轮播链接类型 1：商户入口   2：活动入口
     */
    private Integer type;

    /**
     * 商户ID/活动ID
     */
    private Long typeId;

    private String url;

}
