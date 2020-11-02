package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;

@Data
@TableName("merchant_staff")
public class MerchantStaffEntity {
    @TableId
    private Integer id;
    private Long mchId;
    private String mobile;
    private Integer sType;//1表示管理者，2表示服务员
    private Integer status;
    private String tmpCode;

}
