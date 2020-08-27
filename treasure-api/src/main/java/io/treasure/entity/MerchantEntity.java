package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Data
@TableName("ct_merchant")
public class MerchantEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
    /**
     * 手机号码
     */
	private String mobile;
    /**
     * 商户名称
     */

	private String name;
    /**
     * 商户编码
     */
	private String idcode;
    /**
     * 商户地址
     */
	private String address;
    /**
     * 商户简介
     */
	private String brief;
    /**
     * 营业时间
     */
	private String businesshours;
    /**
     * 经营类别
     */
	private String categoryid;
	/**
	 * 二级经营类别
	 */
	private String categoryidtwo;
    /**
     * 头像
     */
	private String headurl;
	/**
	 * 是否外卖 0 --仅外卖 1 ---即可外卖又可堂食 2---仅堂食
	 */
	private Integer	outside;
	/**
	 * 配送方式 0 ----商家配送 1 ---由平台配送
	 */
	private Integer distribution;
	/**
	 * 配送范围
	 */

	private String deliveryArea;
	/**
     * 评分
     */
	private Double score;
    /**
     * 经度
     */
	private String log;
    /**
     * 纬度
     */
	private String lat;
    /**
     * 营业执照
     */
	private String businesslicense;
    /**
     * 备注
     */
	private String remark;
    /**
     * 状态 0=冻结,1=正常
     */
	private Integer status;
    /**
     * 修改时间
     */@TableField(fill= FieldFill.INSERT_UPDATE)

	private Date updateDate;
	/**
	 * 修改时间
	 */
	private Date createDate;
    /**
     * 修改者
     */
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private Long updater;
    /**
     * 身份证号
     */
	private String cards;

    /**
     * 1-未审核，2-审核通过，3-审核未通过;审核状态
     */
	private Integer auditstatus;
	/**
	 * 闭店时间
	 */
	private String closeshophours;
	/**
	 * 联系电话
	 */
	private String tel;
	/**
	 * 预约天数
	 */
	private Integer days;
	/**
	 * 是否推荐：0否，1是；
	 */
	private Integer recommend;
	/**
	 * 特色菜；多个菜名用英文逗号分隔
	 */
	private String characteristic;
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
	/**
	 * 微信收款人真实姓名
	 */
	private String wxAccountRealname;

	/**
	 * 距离
	 */
	@TableField(exist=false)
	private String distance;
	/**
	 * 平均消费金额
	 */
	private Double monetary;
	/**
	 * 身份证正面照
	 */
	private String idcardFrontImg;
	/**
	 * 身份证反面照
	 */
	private String idcardBackImg;
	/**
	 * 押金金额
	 */
	private Double depost;
	/**
	 *
	 *商家可提现总额
	 */
	private Double totalCash;
	/**
	 *
	 *商家已提现金额
	 */
	private Double alreadyCash;
	/**
	 *
	 *商家未提现金额
	 */

	private Double notCash;

	/**
	 *
	 *审核中金额
	 */
	private BigDecimal wartCash;
	/**
	 *商家扣点总额
	 *
	 */
	private Double pointMoney;
	/**
	 *
	 *审核失败原因
	 */
	private String reason;
	/**
	 *创建者
	 */
	private Long creator;
	/**
	 *月销量
	 */
	private Integer monthySales;

	private Integer party;

	private Integer special;

	private String mchIp;
	private BigDecimal commissionNotWithdraw;
	private BigDecimal commissionAudit;
	private BigDecimal commissionWithdraw;
	private BigDecimal commissionType;

	
}