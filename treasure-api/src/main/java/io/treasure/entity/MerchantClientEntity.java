package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

/**
 * @author user
 * @title: 个推商户cid
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/1812:10
 */
@Data
@TableName("ct_merchant_client")
public class MerchantClientEntity extends BaseEntity {

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 个推cid
     */
    private String clientId;


}
