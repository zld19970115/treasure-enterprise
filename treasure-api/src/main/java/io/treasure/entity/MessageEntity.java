package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 个人消息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_message")
public class MessageEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 业务类型：0 管理，1 商户，2 个人，3 系统，
     */
	private Integer type;
    /**
     * 标题
     */
	private String title;
    /**
     * 内容描述
     */
	private String description;
    /**
     * 已读状态：0-未读，1-已读，9-删除
     */
	private Integer status;
    /**
     * 读取时间
     */
	private Date readTime;
    /**
     * 内容详情、图文
     */
	private String context;
    /**
     * 接收人
     */
	private Long receiver;
    /**
     * 更新时间
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Date updateDate;
    /**
     * 更新者
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Long updater;
}