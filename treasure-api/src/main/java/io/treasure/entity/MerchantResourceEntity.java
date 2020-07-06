package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ct_merchant_resource")
public class MerchantResourceEntity {

    @TableId
    private Long ID;

    private String NAME;

    private String URL;

    private String PERMISSION;

    private Integer TYPE;

    private Long MENU_ID;

    private Integer SEQ;

    private String FUN_NAME;

    private Long PID;

    private Date updateDate;

    private Date createDate;

    private Long creator;

    private Long updater;

    private String icon;

}
