package io.treasure.vo;


import lombok.Data;

@Data
public class HelpSharingActivityVo {

    private Long initiator_id;      //发起助力者的：client_user/id
    private Integer sa_id;          //活动编号

    private String mobile;          //新注册：用户的手机号
    private String password;        //新注册：密码
    private String unionid;         //新注册：unionId
    private String clientId;        //新注册：clientId
}
