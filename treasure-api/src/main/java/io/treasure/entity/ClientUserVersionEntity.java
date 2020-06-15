package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper=false)
@TableName("client_user_version")
public class ClientUserVersionEntity {

    @TableId(value="client_mobile",type = IdType.INPUT)
    private String clientMobile;
    private Integer processType;
    private Long updatePmt;
}
