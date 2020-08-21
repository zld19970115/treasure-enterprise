package io.treasure.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_client_user")
public class ClientUserEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
	private String name;
    /**
     * 昵称
     */
	private String username;
    /**
     * 身份证号码
     */
	private String idCard;
    /**
     * 身份证正面照
     */
	private String idcardFrontImg;
    /**
     * 身份证反面照
     */
	private String idcardBackImg;
    /**
     * 性别 1-男 2-女
     */
	private String sex;
    /**
     * 年龄
     */
	private Integer age;
    /**
     * 出生日期
     */
	private Date birth;
    /**
     * 密码
     */
	private String password;
    /**
     * 手机号
     */
	private String mobile;
    /**
     * 头像图片地址
     */
	private String headImg;
    /**
     * 积分
     */
	private BigDecimal integral;
    /**
     * 余额
     */
	private BigDecimal balance;
    /**
     * 金币
     */
	private BigDecimal coin;
    /**
     * 赠送金
     */
	private BigDecimal gift;
    /**
     * 用户级别
     */
	private Integer level;
    /**
     * 微信用户唯一标识
     */
	private String openid;
    /**
     * 微信unionid
     */
	private String unionid;
    /**
     * 状态 0=冻结/1=正常/9=删除
     */
	private Integer status;
    /**
     * 注册来源 --1点餐APP 2店铺APP 3后台
     */
	private String origin;
    /**
     * 注册方式1手机号 2微信号
     */
	private String way;
    /**
     * 更新时间
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Date updateDate;
    /**
     * 更新者
     */
	/**
	 * 收款支付宝账户
	 */
	private String aliAccountNumber;
	/**
	 * 支付宝收款人真实姓名
	 */
	private String aliAccountRealname;
	/**
	 * 收款微信openid
	 */
	private String wxAccountOpenid;
	/**
	 * 微信绑定方式 1---小程序 2---app
	 */
	private Integer wxStatus;
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private Long updater;
	/**
	 * 个推客户id
	 */
	private String clientId;

	public void setGiftAnotherWay(BigDecimal gift){
		this.gift = gift;
	}
}