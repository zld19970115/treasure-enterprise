package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@Data
@TableName("ct_merchant_room")
public class MerchantRoomEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 包房或者桌名称
     */
	private String name;
    /**
     * 描述
     */
	private String description;
    /**
     * 简介
     */
	private String brief;
    /**
     * 图片
     */
	private String icon;
    /**
     * 可容纳最低人数
     */
	private Integer numLow;
    /**
     * 可容纳最高人数
     */
	private Integer numHigh;
    /**
     * 类型，1-包房，2-桌
     */
	private Integer type;
    /**
     * 商户
     */
	private Long merchantId;
	/**
	 * 排序字段
	 */
	private Integer  sort;
    /**
     * 状态
     */

	private int status;
    /**
     * 修改时间
     */
	private Date updateDate;
    /**
     * 修改者
     */
	private Long updater;
}