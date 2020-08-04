package io.treasure.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsGroup {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String name;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<SimpleDishesVo> list;

}
