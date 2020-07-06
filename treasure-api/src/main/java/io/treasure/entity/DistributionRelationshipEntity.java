package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 *
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("distribution_relationship")
public class DistributionRelationshipEntity {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    private long id;


    /**
     * 主手机号
     */
    private String mobileMaster;

    /**
     * cong手机号
     */
    private String mobileSlaver;
    /**
     * 1:有效，2：失效
     */
    private Integer status;
    /**
     * 关联时间
     */
    private Date unionStartTime;

    /**
     * 关联失效时间
     */
    private Date unionExpireTime;


    /**
     * 活动id
     */
    private Integer saId;

}
