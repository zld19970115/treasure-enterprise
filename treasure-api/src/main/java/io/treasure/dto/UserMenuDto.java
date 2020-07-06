package io.treasure.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserMenuDto {

    private Long id;

    private String name;

    private String url;

    private String icon;

    List<UserMenuDto> children;

}
