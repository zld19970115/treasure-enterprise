package io.treasure.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MerchantPageVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户编码")
    private String idcode;

    @ApiModelProperty(value = "商户地址")
    private String address;

    @ApiModelProperty(value = "商户简介")
    private String brief;

    @ApiModelProperty(value = "营业时间")
    private String businesshours;

    @ApiModelProperty(value = "经营类别")
    private String categoryid;

    @ApiModelProperty(value = "类别名称")
    private String categoryName;

    @ApiModelProperty(value = "头像")
    private String headurl;

    @ApiModelProperty(value = "评分")
    private Double score;

    @ApiModelProperty(value = "经度")
    private String log;

    @ApiModelProperty(value = "纬度")
    private String lat;

    @ApiModelProperty(value = "营业执照")
    private String businesslicense;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 0=冻结,1=正常")
    private Integer status;

    @ApiModelProperty(value = "身份证号")
    private String cards;

    @ApiModelProperty(value = "1-未审核，2-审核通过，3-审核未通过;审核状态")
    private Integer auditstatus;

    @ApiModelProperty(value = "身份证正面照")
    private String idcardFrontImg;

    @ApiModelProperty(value = "身份证反面照")
    private String idcardBackImg;

    @ApiModelProperty(value = "闭店时间")
    private String closeshophours;

    @ApiModelProperty(value = "是否推荐：0否，1是；")
    private String recommend;

    @ApiModelProperty(value = "特色菜；多个菜名用英文逗号分隔")
    private String characteristic;

    @ApiModelProperty(value = "收款微信openid")
    private String wxAccountOpenid;

    @ApiModelProperty(value = "微信收款人真实姓名")
    private String wxAccountRealname;

    @ApiModelProperty(value = "收款支付宝账户")
    private String aliAccountNumber;

    @ApiModelProperty(value = "支付宝收款人真实姓名")
    private String aliAccountRealname;

    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "创建者")
    private Long creator;

    @ApiModelProperty(value = "修改者")
    private Long updater;

    @ApiModelProperty(value = "联系电话")
    private String tel;

    @ApiModelProperty(value = "预约天数")
    private Integer days;

    @ApiModelProperty(value = "平均消费金额")
    private Double monetary;

    @ApiModelProperty(value = "二级分类")
    private String categoryidtwo;

    @ApiModelProperty(value = "押金金额")
    private Double depost;

    @ApiModelProperty(value = "营业总收入")
    private Double totalCash;

    @ApiModelProperty(value = "商家已提现金额")
    private Double alreadyCash;

    @ApiModelProperty(value = "未提现金额(可提现余额)")
    private Double notCash;

    @ApiModelProperty(value = "审核中金额")
    private BigDecimal wartCash;

    @ApiModelProperty(value = "扣点总额")
    private Double pointMoney;

    @ApiModelProperty(value = "审核失败原因")
    private String reason;

    @ApiModelProperty(value = "月销量")
    private Integer monthySales;

    @ApiModelProperty(value = "聚餐")
    private Integer party;

    @ApiModelProperty(value = "特色")
    private Integer special;

    private BigDecimal money;

    private BigDecimal money2;

    private String clientId;

}
