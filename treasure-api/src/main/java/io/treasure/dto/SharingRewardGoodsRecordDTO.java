package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
public class SharingRewardGoodsRecordDTO {

    private Long csrId;
    private Long clientId;
    private Integer status;
    private Integer activityId;
    private Long merchantId;
    private Long goodsId;
    private Integer goodsNum;
    private Date expireTime;
    private Date updatePmt;
    private Integer orderStatus; //订单状态
    private Date consumePmt;    //消费日期

}
