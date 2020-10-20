package io.treasure.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardMchVo {

    private Long mchId;             //商家id
    private Double prizeValue;      //奖励值
    private Double prizePercent;    //奖励百分数
    private Date prizeMonth;        //奖励日期
    private Integer payMethod = 3;//返现的方式
}
