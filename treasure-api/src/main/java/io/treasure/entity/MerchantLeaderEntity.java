package io.treasure.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MerchantLeaderEntity {

    private long id;
    private String mobile;
    private String password;
    private String weixinName;
    private String weixinUrl;
    private String openid;
    private String remark;
    private int status;
    private Date updateDate;
    private Date createDate;
    private long updater;
    private String merchantId;
    private long pid;
    private String pidName;
    private String clientId;

}
