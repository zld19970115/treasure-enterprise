package io.treasure.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrizeUserInfoVo {
    private Long id;
    private String mobile;
    private Double awardsAmount;
}
