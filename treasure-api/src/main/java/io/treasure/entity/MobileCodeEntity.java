package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ct_mobile_code")
public class MobileCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    /**
     * 手机号码
     */
    private String mobile;
    private String code;

}
