package io.treasure.vo;


import lombok.Data;

@Data
public class SharingVo {

    private Long initiator_id;
    private Integer sa_id;
    private String mobile;
    private String password;
    private String unionid;
    private String clientId;
}
