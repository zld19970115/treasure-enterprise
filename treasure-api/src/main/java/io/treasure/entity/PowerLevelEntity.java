package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author user
 * @title: 助力级别
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:02
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_power_level")
public class PowerLevelEntity {

    /**
     * 助力id
     */
    private int id;

    /**
     * 助力id
     */
    private int powerlevelId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 助力奖励类别（1-代付金，2-商品，3-菜品）
     */
    private String powerType;
    /**
     * 获得助力奖励次数
     */
    private int powerGain;
    /**
     * 助力人数
     */
    private int powerSum;
    /**
     * 助力状态（1-助力完成，0-助力中）
     */
    private int powerFinish;
    /**
     * 助力完成时间
     */
    private Date powerAbortDate;
    /**
     * 助力开始时间
     */
    private Date powerOpenDate;
    /**
     * 助力随机数
     */
    private String ramdomNumber;

}
