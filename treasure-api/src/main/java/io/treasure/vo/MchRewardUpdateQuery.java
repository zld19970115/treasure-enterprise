package io.treasure.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MchRewardUpdateQuery {
    private List<Long> ids;
    private Integer status;
    private String comment;
    private Integer method;
}
