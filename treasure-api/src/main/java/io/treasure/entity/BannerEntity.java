package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ct_banner")
public class BannerEntity {

    /**
     * id
     */
    private long id;

    /**
     * 位置排序
     */
    private Integer sort;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 轮播链接类型 1：商户入口   2：活动入口
     */
    private Integer type;

    /**
     * 商户ID/活动ID
     */
    private long typeId;

    /**
     * 更新时间
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
     * 更新者
     */
    private long updater;


}
