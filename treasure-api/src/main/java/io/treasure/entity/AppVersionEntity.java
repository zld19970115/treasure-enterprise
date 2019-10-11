package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_app_version")
public class AppVersionEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * appId
     */
	private String appid;
    /**
     * app版本号
     */
	private String version;
    /**
     * 更新包下载地址
     */
	private String url;
    /**
     * 升级内容
     */
	private String note;
    /**
     * 状态  0：无需升级   1：需要升级
     */
	private Integer status;
    /**
     * 更新者
     */
	private Long updater;
    /**
     * 更新时间
     */
	private Date updateDate;
}