package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ct_merchant_role_resource")
public class MerchantRoleResourceEntity {

    @TableId
    private Long ID;

    private Long ROLE_ID;

    private Long RESOURCE_ID;

}
