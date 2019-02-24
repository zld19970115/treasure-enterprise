package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import java.util.Date;

/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Data
@TableName("ct_agreements")
public class AgreementsEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
    /**
     * 协议内容
     */
	private String content;
    /**
     * 标题
     */
	private String title;
    /**
     * 备注
     */
	private String remark;
    /**
     * 9-删除，1-正常
     */
	private Integer status;
    /**
     * 修改时间
     */
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private Date updateDate;
    /**
     * 更新者
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Long updater;
}