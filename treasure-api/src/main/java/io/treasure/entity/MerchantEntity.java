package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
	private Long categoryid;
    /**
     * 头像
     */
	private String headurl;
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
     * 修改者
     */
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private Long updater;
    /**
     * 身份证号
     */
	private String cards;
	/**
	 * 身份证正面图片
	 */
	private String idcard_front_img;
	/**
	 * 身份证反面图片
	 */
	private String idcard_back_img;
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
	private String recommend;
	/**
	 * 特色菜；多个菜名用英文逗号分隔
	 */
	private String characteristic;

	/**
	 * 距离
	 */
	@TableField(exist=false)
	private String distance;
	/**
	 * 平均消费金额
	 */
	private Double monetary;

}