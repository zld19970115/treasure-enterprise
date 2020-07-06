package io.treasure.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuDto {

    private Long id;

    private String title;

    private Boolean checked;

    private Boolean spread = true;

    private List<RoleMenuDto> children;

}
