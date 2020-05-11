package io.treasure.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class QueryClientUserDto extends ClientUserDTO {
    private int createDateConditionType = 0;
    private Date startTime;
    private Date stopTime;


}
