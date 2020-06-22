package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.util.Date;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("business_manager_track_record")
public class BusinessManagerTrackRecordEntity {

    @TableId(value = "pid",type = IdType.AUTO)
    private Integer pid;                // int(10) auto_increment

    private Integer bmId;               // int(10) not null,业务员id
    private Long mchId;                 // bigint  not null,

    private Date officialEntryTime;     // atetime default CURRENT_TIMESTAMP null comment '入驻时间',

    private Date becomeABusinessTime;   // datetime  null,
    private Integer status;             // int(2)   null comment '1:开始入驻，2审核完成，3营业状态'

}
