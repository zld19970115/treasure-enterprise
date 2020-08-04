package io.treasure.dto;

import lombok.Data;

import java.util.List;

@Data
public class MerchantQueryDto {

    private String names;//名字集合
    private Integer recordsCounter;//查询数量
    private Integer page = 0;
    private Integer num =10;
    private Double longitude;
    private Double latitude;

    private String[] dishes;




}
