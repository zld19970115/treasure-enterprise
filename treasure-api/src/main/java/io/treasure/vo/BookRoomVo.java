package io.treasure.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.jws.HandlerChain;
import java.util.Date;

@Data
@Accessors(chain = true)
public class BookRoomVo {
    private Date sTime;
    private Date eTime;
    private Long id;
}
