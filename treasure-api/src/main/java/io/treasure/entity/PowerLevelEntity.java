package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

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
     * id
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
     * 助力级别（1-一级助力，2-二级助力）
     */
    private String powerlevel;

    /**
     * 助力获得的代付金
     */
    private BigDecimal powerGift;

    /**
     * 助力人数
     */
    private int powerSum;




}
