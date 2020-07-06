package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ct_merchant_user_role")
public class MerchantUserRoleEntity {

    @TableId
    private Long id;

    private Long userId;

    private Long roleId;

}
