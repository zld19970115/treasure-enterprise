package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户赠送金表
 * 2019.9.3
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_user_gift")
public class UserGiftEntity {
/**
 * id
 */
private long id;
/**
 * 卡劵账号
 */
private long number;
    /**
     * 密碼
     */
    private Integer password;
    /**
     * 用戶賬號（手机号）
     */
    private String userNumber;
    /**
     * 状态 0--未使用 1--已使用
     */
    private Integer status;
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
    /**
     * 面额
     */
    private BigDecimal money;



}
