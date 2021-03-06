package io.treasure.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CounterDownVo {

    private Long countDown;
    private Integer status=1;//1活动已结束，2活动进行中，3活动马上开始
    private String msg;

}
