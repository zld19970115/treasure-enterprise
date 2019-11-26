package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ct_opinion")
public class OpinionEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 类型 0---用户 1---商家
     */
    private Integer type;
    /**
     * 留言板
     */
    private String messageBoard;
    /**
     * 反馈图片
     */
    private String  messageImg;

    /**
     * 状态
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
     * 创建者 商户id 或者 用户id
     */
    private long creator;
    /**
     * 修改者
     */
    private long updater;


}
