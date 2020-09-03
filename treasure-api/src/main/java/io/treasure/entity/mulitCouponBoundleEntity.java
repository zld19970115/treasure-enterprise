package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Delete;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mulit_coupon_boundle")
public class mulitCouponBoundleEntity {
    @TableId
    private Long id;
    private String couponId;
    private Long mchId;
    private Long ownerId;
    private Integer type;
    private Integer getMethod;
    private Integer useStatus;
    private Long goodsId;
    private BigDecimal couponValue;
    private Date gotPmt;
    private Date expirePmt;

    private Integer Deleted;

}
