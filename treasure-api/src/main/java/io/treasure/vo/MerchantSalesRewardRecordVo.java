package io.treasure.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSalesRewardRecordVo {

    MerchantSalesRewardRecordEntity merchantSalesRewardRecordEntity;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date stopTime;

    private Integer ranking;
    private Double minValue;
    private Integer index;
    private Integer pagesNum;
}
