package io.treasure.dto;

import lombok.Data;

@Data
public class RoomPCDto {

    private Long id;

    private String name;

    private String description;

    private String brief;

    private String icon;

    private Integer numLow;

    private Integer numHigh;

    private Integer type;

    private Long merchantId;

    private Integer  sort;

    private int status;

    private String createDate;

}
