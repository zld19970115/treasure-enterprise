package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("distribution_params")
public class DistributionParamsEntity {

    @TableId
    private int id;
    private int saId;
    private int radio;

}
