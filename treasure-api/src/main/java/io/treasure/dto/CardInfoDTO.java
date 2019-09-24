package io.treasure.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class CardInfoDTO {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty(value = "分类ID")
    private long id;
    /**
     * 密码
     */
    @ApiModelProperty(value = "分类ID")
    private String password;

    /**
     * 面值
     */
    @ApiModelProperty(value = "分类ID")
    private BigDecimal money;

    /**
     * P批次
     */
    @ApiModelProperty(value = "分类ID")
    private Integer batch;

    /**
     * 类型：1-赠送金
     */
    @ApiModelProperty(value = "分类ID")
    private String type;
    /**
     * 卡状态：1-制卡，2-开卡，3-绑定卡，9-删除
     */
    @ApiModelProperty(value = "分类ID")
    private Integer status;
    /**
     * 开卡时间
     */
    @ApiModelProperty(value = "分类ID")
    private Date openCardDate;
    /**
     * 开卡人
     */
    @ApiModelProperty(value = "分类ID")
    private long openCardUser;
    /**
     * 绑定时间
     */
    @ApiModelProperty(value = "分类ID")
    private Date bindCardDate;
    /**
     * 绑定人
     */

    private long bindCardUser;

    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建者
     */
    private long creator;
    /**
     * 修改者
     */
    private long updater;
}
