package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dao.MerchantUserDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.enm.Role;
import io.treasure.entity.*;
import io.treasure.service.MerchantService;
import io.treasure.service.MerchantUserService;
import io.treasure.service.TokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private MerchantUserService userService;
    @Autowired
    private MerchantService merchantService;
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
        if(user.getStatus()==3){
            throw new RenException("账号已注销");
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

    @Override
    public void updateMerchant(String merchantId, long id) {
        baseDao.updateMerchant(merchantId,id);
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
    public List<MerchantDTO> getMerchantByUserId(Long id) {
        return baseDao.getMerchantByUserId(id);
    }

    /**
     * 根据Id和角色查询店铺
     *  @param id
     * @param role
     * @return
     */
    @Override
    public List<MerchantDTO> getMerchantByUserIdAndRole(Long id, String role) {
        if (StringUtils.isNotEmpty(role) && StringUtils.isNotBlank(role)) {
            if (role.indexOf(Role.ADMIN.getStatus()) > -1) {//管理员查询全部
                return merchantService.getListByOn();
            } else {
                return baseDao.getMerchantByUserId(id);
            }
        }else{
            return null;
        }
    }
    /**
     * 商户会员手机号码查询会员商户信息
     * @param mobile
     * @return
     */
    @Override
    public List getMerchantByMobile(String mobile) {
        return baseDao.getMerchantByMobile(mobile);
    }

    @Override
    public void updateOpenid(String openId, String mobile) {
        baseDao.updateOpenid(openId,mobile);
    }


    @Override
    public MerchantUserEntity getUserByPhone(String mobile) {
        return baseDao.getUserByPhone(mobile);
    }

    @Override
    public MerchantUserEntity getUserByOpenId(String openid) {
        return baseDao.getUserByOpenId(openid);
    }

    @Override
    public void updateCID(String clientId, String mobile) {
        baseDao.updateCID(clientId,mobile);
    }

    @Override
    public PageData<MerchantUserDTO> listPage(Map<String, Object> params) {
        IPage<MerchantUserEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        params.put("mart_id",merchantId);
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("mart_id",null);
        }
        List<MerchantUserDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), MerchantUserDTO.class);
    }


    /**
     * 查询条件
     * @param params
     * @return
     */
    @Override
    public QueryWrapper<MerchantUserEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        String status=(String)params.get("status");
        //微信名称
        String weixinname=(String)params.get("weixinname");
        //手机号码
        String mobile=(String)params.get("mobile");
        //商户
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<MerchantUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.like(StringUtils.isNotBlank(weixinname),"weixinName",weixinname);
        wrapper.eq(StringUtils.isNotBlank(mobile),"mobile",mobile);
        List<Long> mId=new ArrayList<Long>();
        if(StringUtils.isNotBlank(merchantId)){
            String[] mIds=merchantId.split(",");
            for(int i=0;i<mIds.length;i++){
                mId.add(Long.parseLong(mIds[i]));
            }
        }
        wrapper.in(StringUtils.isNotBlank(merchantId),"merchantId",mId);
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

   @Override
    public MerchantUserEntity getByMobiles(String mobile) {
        return baseDao.getByMobile(mobile);
    }

}