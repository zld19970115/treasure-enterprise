package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * id
     */
    private int id;

    /**
     * 助力id
     */
    private int powerlevelId;

    /**
     * 助力人id
     */
    private Long userId;

    /**
     * 助力人id
     */
    private Long powerUserId;

    /**
     * 附加内容
     */
    private String subjoinContent;

    /**
     * 助力类型（1-代付金助力）
     */
    private int powerType;


}
