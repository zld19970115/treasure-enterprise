package io.treasure.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "商户二维码表")
public class MerchantQrCodeDTO implements Serializable {

    /**
     * 商户二维码路径
     */
    @ApiModelProperty(value = "商户二维码路径")
    private String qrUrl;

    /**
     * 需要绑定的银行卡号
     */
    @ApiModelProperty(value = "商户id")
    private Long merchantId;




}
