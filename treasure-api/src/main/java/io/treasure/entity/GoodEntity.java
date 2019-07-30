package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_good")
public class GoodEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 菜品名称
     */
	private String name;
    /**
     * 菜品描述
     */
	private String description;
    /**
     * 菜品条码
     */
	private String barcode;
    /**
     * 菜品原价格
     */
	private BigDecimal price;
    /**
     * 菜品介绍
     */
	private String introduce;
    /**
     * 是否热门 1=热门/0=默认
     */
	private Integer showInHot;
    /**
     * 单位
     */
	private String units;
    /**
     * 备注信息
     */
	private String remarks;
    /**
     * 上架时间
     */
	private Date shelveTime;
    /**
     * 上架人
     */
	private Long shelveBy;
    /**
     * 下架时间
     */
	private Date offShelveTime;
    /**
     * 下架人
     */
	private Long offShelveBy;
    /**
     * 下架原因
     */
	private String offShelveReason;
    /**
     * 商户id
     */
	private Long martId;
    /**
     * 菜品分类id
     */
	private Long goodCategoryId;
    /**
     * 销售量
     */
	private Integer sales;
    /**
     * 购买人数
     */
	private Integer buyers;
    /**
     * 状态(0:禁用，1：使用,9:记录删除)
     */
	private Integer status;
    /**
     * 更新者
     */
	private Long updater;
    /**
     * 更新时间
     */
	private Date updateDate;
    /**
     * 图片
     */
	private String icon;
    /**
     * 优惠价格
     */
	private BigDecimal favorablePrice;
    /**
     * 库存量
     */
	private Integer stock;
}