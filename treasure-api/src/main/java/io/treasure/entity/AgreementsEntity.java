package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_agreements")
public class AgreementsEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

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
	private Date updateDate;
    /**
     * 更新者
     */
	private Long updater;
}