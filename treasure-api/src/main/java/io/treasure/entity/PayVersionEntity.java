package io.treasure.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pay_version")
public class PayVersionEntity {


    private int id;	//int(10)	NO	PRI		auto_increment
    private int orderId;	//varchar(50)	NO	""		""
    private int version;//(10)	YES	""	0	""
    private int serviceType;//	int(2)	NO	""		""
    private int updatePmt;//	datetime	YES	""		on update CURRENT_TIMESTAMP
    private int createPmt;//	datetime	YES	""	CURRENT_TIMESTAMP	""

}
