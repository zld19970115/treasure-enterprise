package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.QueryClientUserDto;
import io.treasure.dto.QueryWithdrawDto;
import io.treasure.entity.ClientUserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
public interface ClientUserService extends CrudService<ClientUserEntity, ClientUserDTO> {

    boolean isRegister(String tel);

    ClientUserEntity getByMobile(String mobile);

    Map<String, Object> login(LoginDTO dto);

    ClientUserEntity getUserByPhone(String mobile);

    ClientUserEntity getUserByOpenId(String openId);

    void updateOpenid(String openId, String mobile);
    void updateUnionid(String openId, String mobile);
    void addRecordGiftByUserid(String userId, String useGift);

    void updateCID(String clientId, String mobile);

    ClientUserEntity getClientUser(Long id);
    Result userGiftToUser(long userId, String mobile, BigDecimal giftMoney) ;
    void subtractGiftByMasterOrderCreate(@Param("creator") Long creator, String gift);
    List<ClientUserEntity> selectZhuXiao(String mobile);
    PageData<ClientUserDTO> getRecordUserAll(Map<String, Object> params);

    List<ClientUserEntity> selectListByCondition(QueryClientUserDto queryClientUserDto);
}