package io.treasure.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * client_level_rule
 * @author 
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("client_level_rule")
public class ClientLevelRuleEntity extends BaseEntity {

    @TableId
    private Integer clr_id;

    /**
     * 满分
     */
    private Integer fullMarks;

    /**
     * 持续时间数，单位(月)
     */
    private Integer durationMonths;

    /**
     * 0新用户参与评定1新用户直接满分一个持续时间
     */
    private Integer newUserFullMarks;

    /**
     * 助力（或活动）协助者数量
     */
    private Integer contactsNumber;

    /**
     * 参考联系人的数量
     */
    private Integer preferenceContactsNumber;

    /**
     * 参考联系人的对应值
     */
    private Integer preferenceContactsValue;

    /**
     * 联系人比重
     */
    private Integer contactsRatio;

    /**
     * 宝币值
     */
    private Integer coins;

    /**
     * 宝币的参考数量
     */
    private Integer preferenceCoinsNumber;

    /**
     * 宝币参考数量对应值
     */
    private Integer preferenceCoinsValue;

    /**
     * 联系人比重
     */
    private Integer coinsRatio;

    /**
     * 单位为元,遇余进位
     */
    private Integer totalConsumption;

    /**
     * 参考消费值,单位为元
     */
    private Integer preferenceConsumtionNumber;

    /**
     * 消费金额参考数量，对应值
     */
    private Integer preferenceConsumtionValue;

    /**
     * 消费比重
     */
    private Integer consumtionRatio;

    private Date update;
}