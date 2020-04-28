package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ClientUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Mapper
public interface ClientUserDao extends BaseDao<ClientUserEntity> {

    ClientUserEntity getUserByMobile(String mobile);
    ClientUserEntity getUserByPhone(String mobile);
    ClientUserEntity getUserByOpenId(String openId);
    void updateOpenid(@Param("openId") String openId,@Param("mobile") String mobile);
    void updateUnionid(@Param("openId") String openId,@Param("mobile") String mobile);
    void updateCID(@Param("clientId") String clientId,@Param("mobile") String mobile);
    ClientUserEntity getClientUser(Long id);
    ClientUserEntity  selectByMobile(String mobile);

    //添加一项
    void subtractGiftByMasterOrderCreate(@Param("creator") Long creator,
                                         @Param("gift") String gift);


}