package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_client_user_collect")
public class ClientUserCollectEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 收藏类型：1-店铺；2-菜品
     */
	private Integer type;
    /**
     * 店铺/菜品id，收藏项id
     */
	private Long collectId;
    /**
     * 用户id
     */
	private Long clientUserId;
    /**
     * 状态：0-未收藏；1-收藏；9-取消收藏
     */
	private Integer status;
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