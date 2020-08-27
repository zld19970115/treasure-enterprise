package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("business_manaher_user")
public class BusinessManaherUserEntity {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private Long bmId;
    private Long userId;
}
