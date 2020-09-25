package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.treasure.common.dao.BaseDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_sales_reward")
public class MerchantSalesRewardEntity{

    private Long id;

    private Integer merchantType;
    private Integer judgeMethod;
    private Integer tradeNum;

    private Integer minimumSales;//          int    附加条件--按名次时的附加条件
    private Integer segmentA;//              int(5)
    private Integer segmentB;//              int(5)
    private Integer segmentC;//              int(5)
    private Integer segmentD;//              int(5)
    private Integer segmentE;//              int(5)
    private Integer rateA;//                 int(5)
    private Integer rateB;//                 int(5)
    private Integer rateC;//                 int(5)
    private Integer rateD;//                 int(5)
    private Integer rateE;//                 int(5)
    private Integer days;                     //天数
    private Integer timeMode; //1下月一号起可返上月的佣金,2星期，下个星期可返上个星期的佣金，3按天数模式每天一返

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatePmt;

}
