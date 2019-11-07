package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dao.ClientUserDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Service
public class ClientUserServiceImpl extends CrudServiceImpl<ClientUserDao, ClientUserEntity, ClientUserDTO> implements ClientUserService {
    @Autowired
    private TokenService tokenService;


    @Override
    public QueryWrapper<ClientUserEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ClientUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public boolean isRegister(String tel) {
        int count=baseDao.selectCount(new QueryWrapper<ClientUserEntity>().eq("mobile",tel));
        if(count==0){
            return false;
        }
        return true;
    }

    @Override
    public ClientUserEntity getByMobile(String mobile) {
        return baseDao.getUserByMobile(mobile);
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        ClientUserEntity user = getByMobile(dto.getMobile());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);

        //密码错误
        if(!user.getPassword().equals(DigestUtils.sha256Hex(dto.getPassword()))){
            throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }

        //获取登录token
        TokenEntity tokenEntity = tokenService.createToken(user.getId());

        Map<String, Object> map = new HashMap<>(2);
        map.put("user",user);
        map.put("token", tokenEntity.getToken());
        map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

        return map;
    }

    @Override
    public ClientUserEntity getUserByPhone(String mobile) {
       return baseDao.getUserByPhone(mobile);
    }

    @Override
    public ClientUserEntity getUserByOpenId(String openId) {
        return baseDao.getUserByOpenId(openId);
    }

    @Override
    public void updateOpenid(String openId,String mobile) {
        baseDao.updateOpenid(openId,mobile);
    }

    @Override
    public void updateCID(String clientId, String mobile) {
        baseDao.updateCID(clientId,mobile);
    }

    @Override
    public ClientUserEntity getClientUser(Long id) {
        return baseDao.getClientUser(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result userGiftToUser(long userId, String mobile, BigDecimal giftMoney) {
        ClientUserEntity clientUserEntity = baseDao.selectById(userId);
        if (clientUserEntity!=null) {
            ClientUserEntity clientUserEntity1 = baseDao.selectByMobile(mobile);
            if (clientUserEntity1!=null){
                BigDecimal gift = clientUserEntity.getGift();
                BigDecimal gift1 = clientUserEntity1.getGift();
                if(gift.compareTo(giftMoney)> -1){
                    gift = gift.subtract(giftMoney);
                    gift1=gift1.add(giftMoney);
                    clientUserEntity.setGift(gift);
                    clientUserEntity1.setGift(gift1);
                    baseDao.updateById(clientUserEntity);
                    baseDao.updateById(clientUserEntity1);
                    return new Result().ok("赠送成功");
                }else {
                    return new Result().error("您得赠送金余额不足");
                }
            }else {
                return new Result().error("该用户没有注册");
            }
     }
        return new Result().error("请重新登录");
    }

}