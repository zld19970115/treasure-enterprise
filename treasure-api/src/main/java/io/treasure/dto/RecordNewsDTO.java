package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "记录表")
public class RecordNewsDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "记录ID")
    private Long id;
    /**
     * 新闻iD
     */
    @ApiModelProperty(value = "新闻iD")
    private Long nId;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long uId;
    /**
     * 类型 1商户端 2用户端
     */
    @ApiModelProperty(value = "类型 1--用户 2--商户")
    private int status;
}
