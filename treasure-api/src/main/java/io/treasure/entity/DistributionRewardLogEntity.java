package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

/**
 *
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("distribution_reward_log")
public class DistributionRewardLogEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private long id;

    private String mobileSlaver;
    private String mobileMaster;
    /**
     * 1:宝币，2：代付金，3现金
     */
    private Integer rewardType;
    /**
     * 参考总金额，单位为分
     */
    private Integer referencesTotal;
    /**
     * 例：80代表百分之80
     */
    private Integer rewardRatio;
    /**
     * 奖励数量，单位为分
     */
    private Integer rewardAmount;
    /**
     * 消费时间
     */
    private Date consumeTime;

}
