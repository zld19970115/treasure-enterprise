package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.CategoryEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "分类附加")
public class AttachCategoryVo{
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "ID")
        private Long id;
        @ApiModelProperty(value = "分类名称")
        private String name;

}
