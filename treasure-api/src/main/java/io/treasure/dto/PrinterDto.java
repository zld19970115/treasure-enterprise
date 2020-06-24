package io.treasure.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PrinterDto {

    private Long id;

    private String name;

    private Integer state;

    private Long mid;

    private Date createDate;

}
