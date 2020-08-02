package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "分类附加")
public class AttachCategoryPlusVo {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "商户名称")
        private String merchantName;
        @ApiModelProperty(value = "分类名称")
        private String name;

}
