package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户广告位推广
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@TableName("ct_merchant_advert_extend")
public class MerchantAdvertExtendEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 广告图片
     */
	private String url;
    /**
     * 广告链接
     */
	private String link;
    /**
     * 商户
     */
	private Long merchantId;
    /**
     * 备注
     */
	private String remark;
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