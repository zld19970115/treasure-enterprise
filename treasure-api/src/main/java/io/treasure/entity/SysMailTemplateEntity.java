/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮件模板
 * 
 * @author super 63600679@qq.com
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_mail_template")
public class SysMailTemplateEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long id;
	/**
	 * 模板名称
	 */
	private String name;
	/**
	 * 邮件主题
	 */
	private String subject;
	/**
	 * 邮件正文
	 */
	private String content;

}