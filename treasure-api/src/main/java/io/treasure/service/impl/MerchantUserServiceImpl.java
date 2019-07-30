package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dao.MerchantUserDao;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.entity.MerchantUserEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.MerchantUserService;
import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户管理员
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
@Service
public class MerchantUserServiceImpl extends CrudServiceImpl<MerchantUserDao, MerchantUserEntity, MerchantUserDTO> implements MerchantUserService {
    @Autowired
    private TokenService tokenService;
    /**
     * 登陆
     * @param loginDTO
     * @return
     */
    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        MerchantUserEntity user=baseDao.getByMobile(loginDTO.getMobile());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);
        //密码错误
        if(!user.getPassword().equals(DigestUtils.sha256Hex(loginDTO.getPassword()))){
            throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        //获取登录token
        TokenEntity tokenEntity = tokenService.createToken(user.getId());
        Map<String, Object> map = new HashMap<>(2);
        map.put("token", tokenEntity.getToken());
        map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());
        map.put("mobile",user.getMobile());
        map.put("id",user.getId());
        map.put("weixinUrl",user.getWeixinurl());
        map.put("weixinName",user.getWeixinname());
        return map;
    }

    /**
     * 修改密码
     * @param password
     * @param id
     */
    @Override
    public void updatePassword(String password, long id) {
        baseDao.updatePassword(password,id);
    }

    /**
     * 修改手机号
     * @param mobile
     * @param id
     */
    @Override
    public void updateMobile(String mobile, long id) {
        baseDao.updateMobile(mobile,id);
    }

    /**
     * 帮定微信
     * @param openid
     * @param weixinname
     * @param weixinurl
     * @param id
     */
    @Override
    public void updateWeixin(String openid, String weixinname, String weixinurl, long id) {
        baseDao.updateWeixin(openid,weixinname,weixinurl,id);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void remove(Long id) {
        baseDao.remove(id);
    }

    /**
     * 根据会员Id查询店铺信息
     * @param id
     * @return
     */
    @Override
    public List getMerchantByUserId(Long id) {
        return baseDao.getMerchantByUserId(id);
    }

    @Autowired
    private MerchantUserService userService;

    /**
     * 查询条件
     * @param params
     * @return
     */
    @Override
    public QueryWrapper<MerchantUserEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        String status=(String)params.get("status");
        QueryWrapper<MerchantUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        return wrapper;
    }

    /**
     * 根据手机号码/注册账号查询
     * mobile 手机号码/注册账号
     * @return
     */
    @Override
    public MerchantUserEntity getByMobile(String mobile) {
        return baseDao.getByMobile(mobile);
    }

}