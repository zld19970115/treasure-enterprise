package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("distribution_reward_log")
public class DistributionRewardLogEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private long id;

    private String mobile_slaver;
    private String mobile_master;
    /**
     * 1:宝币，2：代付金，3现金
     */
    private Integer reward_type;
    /**
     * 参考总金额，单位为分
     */
    private Integer references_total;
    /**
     * 例：80代表百分之80
     */
    private Integer reward_ratio;
    /**
     * 奖励数量，单位为分
     */
    private Integer reward_amount;
    /**
     * 消费时间
     */
    private Date consume_time;

}
