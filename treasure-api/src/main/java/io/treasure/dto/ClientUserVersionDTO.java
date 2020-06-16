package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ApiModel(value = "用户操作锁-临时")
@Accessors(chain = true)
public class ClientUserVersionDTO {

    private String clientMobile;
    private Integer processType;
    private Long updatePmt;
}
