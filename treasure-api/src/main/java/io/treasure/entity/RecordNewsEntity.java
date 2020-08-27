package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_record_news")
public class RecordNewsEntity {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    /**
     * 新闻iD
     */
    private Long nId;
    /**
     * 用户id
     */
    private Long uId;
    /**
     * 类型 1商户端 2用户端
     */
    private int status ;
}
