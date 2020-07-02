package io.treasure.dto;

import lombok.Data;

@Data
public class MerchantResourceShowDto {

    private Long id;

    private String title;

    private String url;

    private Long menuId;

    private Integer sort;

    private Long pid;

    private String icon;

}
