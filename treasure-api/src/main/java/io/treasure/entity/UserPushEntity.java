package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_push")
public class UserPushEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private Long userId;

    private String clientId;

    private String meId;

    private String meName;

    private Date createTime;

}
