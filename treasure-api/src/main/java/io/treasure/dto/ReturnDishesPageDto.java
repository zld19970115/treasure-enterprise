package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@ApiModel(value = "退菜列表DTO")
public class ReturnDishesPageDto {

    private Long merchantId;

    private String orderId;

    private Integer page = 1;

    private Integer limit = 10;

}
