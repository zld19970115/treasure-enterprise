package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ct_merchant_role")
public class MerchantRoleEntity {

    @TableId
    private Long id;

    private String name;

    private String sn;

    private String descr;

    private Long pid;

}
