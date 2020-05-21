package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.entity.MerchantUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户管理员
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
public interface MerchantUserService extends CrudService<MerchantUserEntity, MerchantUserDTO> {
    //根据 手机号码/注册账号查询
    MerchantUserEntity getByMobile(String mobile);
    MerchantUserEntity getByMobiles(String mobile);
    boolean isRegister(String tel);
    //用户登陆
    Map<String, Object> login(LoginDTO loginDTO);

    //修改密码
    void updatePassword(String password, long id);

    //修改手机号
    void updateMobile(String mobile, long id);

    //帮定微信
    void updateWeixin(String openid, String weixinName, String weixinUrl, long id);

    //设置店铺
    void updateMerchant(String merchantId, long id);

    //删除
    void remove(Long id);

    //会员id查询该会员对应的商户信息
    List<MerchantDTO> getMerchantByUserId(Long id);

    List<MerchantDTO> getMerchantByUserIdAndRole(Long id, String role);

    //根据会员手机号码查询对应的商户信息
    List getMerchantByMobile(String mobile);

    //通过手机号更新用户openid
    void updateOpenid(String openId, String mobile);
    void updateMiniOpenid(String openId, String mobile);

    //通过手机号查询商户信息
    MerchantUserEntity getUserByPhone(String mobile);

    //通过openid查询商户管理员信息
    MerchantUserEntity getUserByOpenId(@Param("openid") String openid);

    void updateCID(String clientId,String mobile);
    PageData<MerchantUserDTO> listPage(Map<String,Object> params);

}