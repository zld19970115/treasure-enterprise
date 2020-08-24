package io.treasure.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo {

    @TableField("name")
    private String gname;
    @TableField("icon")
    private String gicon;

}
