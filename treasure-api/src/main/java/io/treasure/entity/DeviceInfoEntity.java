package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("device_info")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeviceInfoEntity{

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String clientId;
    private String deviceToken;
    private Integer groupId;
    private Integer deviceStatus;
    private Date updateTime;


}
