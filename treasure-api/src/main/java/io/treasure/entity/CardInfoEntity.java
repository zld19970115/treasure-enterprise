package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_card_info")
public class CardInfoEntity  extends BaseEntity {
    private static final long serialVersionUID = 1L;
/**
 * id
 */
private long id;
    /**
     * 密码
     */
    private String password;

    /**
     * 面值
     */
    private BigDecimal money;

    /**
     * P批次
     */

    private Integer batch;

    /**
     * 类型：1-赠送金
     */

    private String type;
    /**
     * 卡状态：1-制卡，2-开卡，3-绑定卡，9-删除
     */

    private Integer status;
    /**
     * 开卡时间
     */

    private Date openCardDate;
    /**
     * 开卡人
     */

    private Date openCardUser;
    /**
     * 绑定时间
     */

    private Date bindCardDate;
    /**
     * 绑定人
     */

    private Date bindCardUser;

    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建者
     */
    private long creator;
    /**
     * 修改者
     */
    private long updater;
}
