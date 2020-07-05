package io.treasure.vo;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HelpSharingActivityVo {

    //@NotNull
    private Long initiator_id;      //发起助力者的：client_user/id
    //@NotNull
    private Integer sa_id;          //活动编号

    //@NotNull
    private String mobile;          //新注册：用户的手机号
    //@NotNull
    private String password;        //新注册：密码
    private String unionid;         //新注册：unionId
    private String clientId;        //新注册：clientId
}
