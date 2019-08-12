package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户端包房参数管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Data

@TableName("ct_merchant_room_params")
public class MerchantRoomParamsEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 内容
     */
	private String content;
    /**
     * 1-包房/桌
     */
	private Integer type;
    /**
     * 商户编号
     */
	private Long merchantId;
    /**
     * 状态
     */
	private Integer status;
    /**
     * 修改时间
     */
	private Date updateDate;
    /**
     * 修改者
     */
	private Long updater;
}