/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短信
 * 
 * @author super 63600679@qq.com
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_sms")
public class SysSmsEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long id;
	/**
	 * 平台类型
	 */
	private Integer platform;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 参数1
	 */
	@TableField("params_1")
	private String params1;
	/**
	 * 参数2
	 */
	@TableField("params_2")
	private String params2;
	/**
	 * 参数3
	 */
	@TableField("params_3")
	private String params3;
	/**
	 * 参数4
	 */
	@TableField("params_4")
	private String params4;
	/**
	 * 发送状态  0：失败   1：成功
	 */
	private Integer status;

}