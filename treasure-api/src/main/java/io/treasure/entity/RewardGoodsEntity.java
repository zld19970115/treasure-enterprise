package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("reward_goods")
public class RewardGoodsEntity {

    @TableId
    private Long id;                    //bigint unsigned auto_increment comment '菜品ID'  primary key,
    private String name;                //char(32)          default ''   not null   comment '菜品名称',
    private Long mch_id;                //bigint            default 0    null       comment '商户id',
    private Long dishes_id;             //bigint                         null       comment '菜品id',
    private Integer status;             //int(2)            default 0    null comment '状态(0禁用，1使用)',
    private Long catelog_id;            //暂时不用独立分类，后面根据需求再添加
    private BigDecimal price;           //decimal(10, 2)    default 0.00 null comment '菜品原价格',
    private String units;               //char(10)                      null comment '单位',
    private String icon;                //char(255)         default ''  null comment '图片',
    private String qrcode;              //char(255)                     null comment '二维码',
    private String description;         //char(128)         default ''  null comment '菜品描述',
    private String remarks;             //char(128)         default ''  null comment '备注信息',

    private Integer deleted;            //int(2)            default 0   null comment '逻辑删除',
    private Date update_date;           //datetime                      null comment '更新时间'
}
