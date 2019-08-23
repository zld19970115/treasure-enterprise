package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 评价表
 * 2019.8.21
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_evaluate")
public class EvaluateEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 评价ID
     */
    private long id;

    /**
     *环境卫生
     */
    private Integer hygiene;
    /**
     *服务态度
     */
    private Integer attitude;
    /**
     *菜品口味
     */
    private Integer flavor;
    /**
     *用餐价格
     */
    private Integer price;
    /**
     *上菜速度
     */
    private Integer speed;
    /**
     *意见建议
     */
    private String proposal;
    /**
     *用户id
     */
    private long uid;
    /**
     * 商户id
     */
    private long martId;
    /**
     * 总评分
     */
    private Integer totalScore;
    /**
     * 订单id
     */
    private long masterorderId;
    /**
     * 状态 0=冻结,1=正常
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
}
