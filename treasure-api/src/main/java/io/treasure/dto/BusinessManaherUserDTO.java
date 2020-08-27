package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "业务员用户关联表")
public class BusinessManaherUserDTO  implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "bmId")
    private Long bmId;
    @ApiModelProperty(value = "userId")
    private Long userId;
}
