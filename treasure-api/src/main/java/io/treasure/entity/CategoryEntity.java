package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_category")
public class CategoryEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 上级分类ID
     */
	private Long pid;
    /**
     * 分类名称
     */
	private String name;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 状态 1=显示/0=隐藏
     */
	private Integer status;
    /**
     * 是否推荐（0：不推荐 1：推荐）
     */
	private Integer showInCommend;
    /**
     * 是否导航栏 1：显示  0：隐藏
     */
	private Integer showInNav;
    /**
     * 分类小图标
     */
	private String icon;
    /**
     * 备注信息
     */
	private String remarks;
    /**
     * 更新者
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Long updater;
    /**
     * 更新时间
     */
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private Date updateDate;
}