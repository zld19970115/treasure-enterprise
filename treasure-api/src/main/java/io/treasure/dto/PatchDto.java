package io.treasure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatchDto implements Comparable<PatchDto>{

    BigDecimal mainValue;       //在分数记录列表
    Integer truncatedDecimal;   //小数参数值
    Integer id;

    public PatchDto(BigDecimal mainValue, Integer truncatedDecimal) {
        this.mainValue = mainValue;
        this.truncatedDecimal = truncatedDecimal;
    }

    @Override
    public int compareTo(PatchDto o) {
        if(this.truncatedDecimal>o.getTruncatedDecimal())
            return 1;
        if(this.truncatedDecimal<o.getTruncatedDecimal())
            return -1;
        return 0;
    }


}
