package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户二维码
 * 2020.5.9
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_merchant_qrcode")
public class MerchantQrCodeEntity {

    /**
     * id
     */
    private int id;

    /**
     * 商户二维码路径
     */
    private String qrUrl;

    /**
     * 商户id
     */
    private Long merchantId;
    /**
     * 二维码生成时间
     */
    private Date creatTime;

}
