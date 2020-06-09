package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author user
 * @title: 助力内容
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:02
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_power_content")
public class PowerContentEntity {

    /**
     * 助力id
     */
    private int id;

    /**
     * 助力id
     */
    private int powerlevelId;
    /**
     * 助力活动名称
     */
    private String powerName;
    /**
     * 助力奖励类别（1-代付金，2-商品，3-菜品）
     */
    private int powerType;
    /**
     * 助力奖励数量
     */
    private int powerSum;
    /**
     * 助力类型
     */
    private int powerPeopleSum;
    /**
     * 活动内容
     */
    private String subjoinContent;
    /**
     * 菜品id
     */
    private Long goodId;
    /**
     * 商品id
     */
    private Long merchandiseId;
    /**
     * 活动开始时间
     */
    private Date activityOpenDate;
    /**
     * 活动截止日期
     */
    private Date activityAbortDate;

}
