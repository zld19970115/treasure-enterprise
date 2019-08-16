package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
@Data
@TableName("ct_merchant_room_params_set")
public class MerchantRoomParamsSetEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 参数编号
     */
	private Long roomParamsId;
    /**
     * 包房编号
     */
	private Long roomId;
    /**
     * 1-包房，2-桌
     */
	private Integer type;
    /**
     * 商户编号
     */
	private Long merchantId;
    /**
     * 使用状态，0未使用,1已使用
     */
	private Integer state;
    /**
     * 状态，9-删除，1-显示，0-隐藏
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;
    /**
     * 使用时间
     */
	private Date useDate;
    /**
     * 修改时间
     */
	private Date updateDate;
    /**
     * 修改者
     */
	private Long updater;
	/**
	 * 包房名称
	 */
	private String roomName;
}