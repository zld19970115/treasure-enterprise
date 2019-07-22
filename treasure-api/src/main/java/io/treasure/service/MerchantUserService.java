package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.entity.MerchantUserEntity;
import java.util.Map;
import java.util.List;
/**
 * 商户管理员
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
public interface MerchantUserService extends CrudService<MerchantUserEntity, MerchantUserDTO> {
    //根据 手机号码/注册账号查询
    MerchantUserEntity getByMobile(String mobile);
    //用户登陆
    Map<String,Object> login(LoginDTO loginDTO);
    //修改密码
    void updatePassword(String password,long id);
}