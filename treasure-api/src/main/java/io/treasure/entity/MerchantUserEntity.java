package io.treasure.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;

/**
 * 商户管理员
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
@Data
@TableName("ct_merchant_user")
public class MerchantUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
    /**
     * 手机号码
     */
	private String mobile;
    /**
     * 密码
     */
	@JSONField(serialize=false)
	private String password;
    /**
     * 微信名称
     */
	private String weixinname;
    /**
     * 微信头像
     */
	private String weixinurl;
    /**
     * 
     */
	private String openid;
    /**
     * 备注
     */
	private String remark;
    /**
     * 状态
     */
	private Integer status;
    /**
     * 修改时间
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Date updateDate;
    /**
     * 修改者
     */
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private Long updater;
    /**
     * 商户编号
     */
	private Long merchantid;
	private Date createDate;
	private Long creator;
}