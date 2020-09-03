package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("advertisement")
public class AdvertisementEntity {

    @TableId
    private Long id;

    private String image;

    private Integer type;

}
