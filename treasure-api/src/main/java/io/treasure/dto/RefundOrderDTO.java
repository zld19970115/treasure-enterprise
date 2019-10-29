package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "退菜订单")
public class RefundOrderDTO {
    private static final long serialVersionUID = 1L;
    /**
     * 退菜ID
     */
    private String refundId;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 菜品ID
     */
    private long goodId;
    /**
     * 商户ID
     */
    private long merchantId;
    /**
     * 退菜数量
     */
    private BigDecimal refundQuantity;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 总价
     */
    private BigDecimal totalMoney;
    /**
     * 退菜时间
     */
    private String refundDate;
    /**
     * 退菜原因
     */
    private String refundReason;
    /**
     * 是否已处理 默认1-未处理，2-已处理
     */
    private Integer dispose;
    /**
     * 用户电话
     */
    private String contactNumber;
    /**
     * 商品名称
     */
    private String goodName;
    /**
     * 商品图片
     */
    private String icon;
    /**
     * 包房名称
     */
    private String roomName;

    /**
     * 订单总金额
     */
    private String totalFee;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户ID
     */
    private String payMode;

}
