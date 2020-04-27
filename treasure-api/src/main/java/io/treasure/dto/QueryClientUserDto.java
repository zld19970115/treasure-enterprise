package io.treasure.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class QueryClientUserDto extends ClientUserDTO {

    private int integralConditionType = 0;
    private int coinConditionType = 0;
    private int giftConditionType = 0;
    private int levelConditionType = 0;

    private int createDateConditionType = 0;
    private Date startTime;
    private Date stopTime;


}
