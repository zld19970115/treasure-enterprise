package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 制卡表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-09-21
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_card_make")
public class CardMakeEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long id;
    /**
     * 制卡数量
     */
	private Integer cardNum;
    /**
     * 制卡面值
     */
	private BigDecimal cardMoney;
    /**
     * 制卡批次
     */
	private Integer cardBatch;
    /**
     * 制卡类型：1-赠送金
     */
	private String cardType;
    /**
     * 更新者
     */
	private Long updater;
    /**
     * 更新时间
     */
	private Date updateDate;

	private Long creator;
}