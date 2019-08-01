package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_merchant_activity")
public class MerchantActivityEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
	private String title;
    /**
     * 活动内容
     */
	private String content;
    /**
     * 简介
     */
	private String brief;
    /**
     * 图片
     */
	private String icon;
    /**
     * 类型
     */
	private Integer type;
    /**
     * 商户
     */
	private Long merchantId;
    /**
     * 状态
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;
    /**
     * 修改时间
     */
	private Date updateDate;
    /**
     * 
     */
	private Long updater;
}