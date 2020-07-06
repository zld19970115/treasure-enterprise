package io.treasure.dto;

import lombok.Data;

@Data
public class MerchantResourceSaveDto {

    private Long id;

    private String name;

    private String url;

    private Long menuId;

    private Integer seq;

    private Long pid;

    private String icon;

    private Integer type;

}
