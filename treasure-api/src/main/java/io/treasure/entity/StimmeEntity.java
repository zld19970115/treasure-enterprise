package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_stimme")
public class StimmeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 类型 1---生成订单
     */
    private Integer type;

    /**
     * 状态 0 ---未查看 1----已查看
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
    private Long creator;
    /**
     * 修改者
     */
    private Long updater;
}
