package io.treasure.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MulitCouponBoundleNewDto {
    private Long id;
    private String couponId;
    private Long mchId;
    private Long ownerId;
    private Integer type;
    private Integer getMethod;
    private Integer useStatus;
    private Long goodsId;
    private BigDecimal couponValue;
    private BigDecimal consumeValue;
    private String processingNo;
    private Date gotPmt;
    private Date expirePmt;
    private Integer Deleted;
    private String mobile;
    private Integer flag;

}
