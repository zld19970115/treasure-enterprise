package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MerchantUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户管理员
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
@Mapper
public interface MerchantUserDao extends BaseDao<MerchantUserEntity> {
    MerchantUserEntity getByMobile(String mobile);
    //修改密码
    void updatePassword(String password,long id);
    //修改手机号码
    void updateMobile(String password,long id);
    //帮定微信
    void updateWeixin(String openid,String weixinName,String weixinUrl,long id);
    //设置店铺
    void updateMerchant(String merchantid,long id);
    //删除
    void remove(long id);
    //根据会员id查询商户信息
    List<MerchantDTO> getMerchantByUserId(@Param("id") Long id);
    //根据手机号码查询会员商户信息
    List getMerchantByMobile(String mobile);
    //更新用户手机号
    void updateOpenid(@Param("openId") String openId,@Param("mobile") String mobile);
    //通过手机号查询商户信息
    MerchantUserEntity getUserByPhone(@Param("mobile") String mobile);
    //通过openid查询商户管理员信息
    MerchantUserEntity getUserByOpenId(@Param("openid") String openid);
    void updateCID(@Param("clientId") String clientId,@Param("mobile") String mobile);
    List<MerchantUserDTO> listPage(Map<String,Object> params);
}