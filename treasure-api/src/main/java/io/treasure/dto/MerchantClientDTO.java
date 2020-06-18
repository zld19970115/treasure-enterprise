package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author user
 * @title: 个推商户cid
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/1812:12
 */
@Data
@ApiModel(value = "个推商户cid")
public class MerchantClientDTO {

    @ApiModelProperty(value = "商户id")
    private long merchantId;

    @ApiModelProperty(value = "个推cid")
    private String clientId;



}
