package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dao.ClientUserDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.LoginDTO;
import io.treasure.dto.QueryClientUserDto;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.LevelStatusEntity;
import io.treasure.entity.RecordGiftEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.RecordGiftService;
import io.treasure.service.TokenService;
import io.treasure.vo.AppLoginCheckVo;
import io.treasure.vo.MerchantAccountVo;
import io.treasure.vo.PageTotalRowData;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private RecordGiftService recordGiftService;
    @Autowired(required = false)
    private ClientUserDao clientUserDao;

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
    public void updateWX(String userId) {
        baseDao.updateWX(userId);
    }

    @Override
    public Result login(String mobile,String unionid) {
        ClientUserEntity user = getByMobile(mobile);
        Map<String, Object> map = new HashMap<>(2);
       if (user==null){
           ClientUserEntity user1 =  new ClientUserEntity();
           user1.setLevel(1);
           user1.setMobile(mobile);
           user1.setUsername(mobile);
           user1.setGift(new BigDecimal("50"));
           user1.setCreateDate(new Date());
           if (unionid!=null){
               user1.setUnionid(unionid);
           }

           baseDao.insert(user1);
           ClientUserEntity userByPhone1 = baseDao.getUserByPhone(mobile);
           tokenService.createToken(userByPhone1.getId());
           map.put("user", userByPhone1);
           TokenEntity byUserId = tokenService.getByUserId(userByPhone1.getId());
           map.put("token", byUserId.getToken());
           map.put("expire", byUserId.getExpireDate().getTime() - System.currentTimeMillis());
           return new Result().ok(map);
       }else {
           //获取登录token
           TokenEntity tokenEntity = tokenService.createToken(user.getId());
           map.put("user",user);
           map.put("token", tokenEntity.getToken());
           map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

           return new Result().ok(map);
       }

    }

    @Override
    public String selectZSCoinTotx() {
        return baseDao.selectZSCoinTotx();
    }

    @Override
    public String selectCoinToBalance() {
        return baseDao.selectCoinToBalance();
    }

    @Override
    public List<LevelStatusEntity> selectLevelStatus(long userId) {
        return baseDao.selectLevelStatus(userId);
    }

    @Override
    public BigDecimal selectBlanceForLevel(int level) {
        return baseDao.selectBlanceForLevel(level);
    }

    @Override
    public String selectpictureForLevel(int level) {
        return baseDao.selectpictureForLevel(level);
    }

    @Override
    public ClientUserEntity getUserByPhone(String mobile) {
       return baseDao.getUserByPhone(mobile);
    }

    @Override
    public void insertLevelStatus(Long userId, Integer level, BigDecimal balance) {
        baseDao.insertLevelStatus(userId,level,balance);
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
    public void updateUnionid(String openId, String mobile) {
        baseDao.updateUnionid(openId,mobile);
    }

    @Override
    public void addRecordGiftByUserid(String userId, String useGift) {
        baseDao.addRecordGiftByUserid(userId,useGift);
    }

    @Override
    public void updateBynewCoin(long userId,BigDecimal newCoin) {
        baseDao.updateBynewCoin(userId,newCoin);
    }

    @Override
    public void addCoinsByUserid(String userId, String useGift) {
        baseDao.addCoinsByUserid(userId,useGift);
    }

    @Override
    public void addBalanceByUserid(String userId, String baobi) {
        baseDao.addBalanceByUserid(userId,baobi);
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
        String s = giftMoney.toString();
        String[] split = s.split("\\.");
        System.out.println(split);
        BigDecimal c=new BigDecimal("0");
        if(split.length==1 && giftMoney.compareTo(c)==1) {
            ClientUserEntity clientUserEntity = baseDao.selectById(userId);
            if (clientUserEntity != null) {
                ClientUserEntity clientUserEntity1 = baseDao.selectByMobile(mobile);
                if (clientUserEntity1 != null) {
                    if (clientUserEntity1.getId()==clientUserEntity.getId() || clientUserEntity1.getId().equals(clientUserEntity.getId()) ){
                        return new Result().error("不能赠送给自己");
                    }
                    BigDecimal mobileGift = clientUserEntity1.getGift();
                    BigDecimal info = new BigDecimal(10000);
                    if(mobileGift!=null){
                        if (mobileGift.compareTo(info) == 1){
                            return new Result().error("对方用户代付金大于一万元不允许赠送");
                        }
                    }
                    BigDecimal gift = clientUserEntity.getGift();
                        BigDecimal gift1 = clientUserEntity1.getGift();
                        if (gift.compareTo(giftMoney) > -1) {
                            Date date = new Date();
                            gift = gift.subtract(giftMoney);
                            gift1 = gift1.add(giftMoney);
                            clientUserEntity.setGift(gift);
                            clientUserEntity1.setGift(gift1);
                            baseDao.updateById(clientUserEntity);
                            baseDao.updateById(clientUserEntity1);
                            RecordGiftEntity recordGiftEntity = new RecordGiftEntity(); //转移记录
                            RecordGiftEntity recordGiftEntityed = new RecordGiftEntity();// 被转移记录
                            recordGiftEntity.setBalanceGift(gift);
                            recordGiftEntity.setCreateDate(date);
                            recordGiftEntity.setUserId(userId);
                            recordGiftEntity.setTransferredMobile(mobile);
                            recordGiftEntity.setUseGift(giftMoney);
                            recordGiftEntity.setStatus(3);
                            recordGiftEntityed.setUseGift(giftMoney);
                            recordGiftEntityed.setUserId(clientUserEntity1.getId());
                            recordGiftEntityed.setCreateDate(date);
                            recordGiftEntityed.setBalanceGift(gift1);
                            recordGiftEntityed.setTransferredMobile(clientUserEntity.getMobile());
                            recordGiftEntityed.setStatus(4);
                            recordGiftService.insert(recordGiftEntity);
                            recordGiftService.insert(recordGiftEntityed);
                            return new Result().ok("赠送成功");

                    } else {
                        return new Result().error("您的代付金余额不足");
                    }
                } else {
                    return new Result().error("对方用户没有注册");
                }
            }
        }else {
            return new Result().error("代付金额不能包含小数且必须大于0");
        }
        return new Result().error("请重新登录");
    }



    @Override
    public void subtractGiftByMasterOrderCreate(Long creator, String gift) {
        clientUserDao.subtractGiftByMasterOrderCreate(creator,gift);
    }

    @Override
    public List<ClientUserEntity> selectZhuXiao(String mobile) {
        return baseDao.selectZhuXiao(mobile);
    }

    @Override
    public List<ClientUserEntity> selectListByCondition(QueryClientUserDto queryClientUserDto) {
        return null;
    }

    @Override
    public PageTotalRowData<ClientUserDTO> pagePC(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<ClientUserDTO> page = (Page) clientUserDao.pagePC(params);
        Map map = new HashMap();
        if(page != null && page.getResult().size() > 0) {
            ClientUserDTO vo = baseDao.pagePCTotalRow(params);
            if(vo != null) {
                map.put("integral",vo.getIntegral());
                map.put("balance",vo.getBalance());
                map.put("gift",vo.getGift());
            }
        }
        return new PageTotalRowData<>(page.getResult(),page.getTotal(),map);
    }

    @Override
    public PageData<ClientUserDTO> getRecordUserAll(Map<String, Object> params) {
        //分页
        IPage<ClientUserEntity> page = getPage(params, Constant.CREATE_DATE, false);

        //查询
        List<ClientUserDTO> list = baseDao.getRecordUserAll(params);

        return getPageData(list, page.getTotal(), ClientUserDTO.class);
    }
    @Override
    public int getLogOffCount(String mobile){
        int res = 0;
        Integer tmp = clientUserDao.selectLogOffCount(mobile+"已注销");
        if (tmp != null)
            res = tmp;
        return res;
    }

    private ClientUserEntity registerDefault(ClientUserEntity user){
        BigDecimal rewardRegister = new BigDecimal("50");
        user.setLevel(1);
        user.setGift(new BigDecimal("50"));
        return user;
    }
    private Result userRegister(AppLoginCheckVo vo){
        Map<String, Object> map = new HashMap<>(2);
        ClientUserEntity user =  new ClientUserEntity();
        user.setLevel(1);
        user.setMobile(vo.getMobile());
        if(vo.getNickName() != null){
            user.setUsername(vo.getNickName());
        }else{
            user.setUsername(vo.getMobile());
        }
        if(vo.getHeadIcon() != null){
            user.setHeadImg(vo.getHeadIcon());
        }
        if(vo.getUnionid() != null){
            user.setUnionid(vo.getUnionid());
        }
        user.setGift(new BigDecimal("50"));
        user.setCreateDate(new Date());
        baseDao.insert(user);

        ClientUserEntity userByPhone = baseDao.getUserByPhone(vo.getMobile());

        tokenService.createToken(userByPhone.getId());
        map.put("user", userByPhone);
        TokenEntity byUserId = tokenService.getByUserId(userByPhone.getId());
        map.put("token", byUserId.getToken());
        map.put("expire", byUserId.getExpireDate().getTime() - System.currentTimeMillis());
        return new Result().ok(map);
    }
    private Result loginSuccess(ClientUserEntity user){

        Map<String, Object> map = new HashMap<>(2);
        TokenEntity tokenEntity = tokenService.createToken(user.getId());
        map.put("user",user);
        map.put("token", tokenEntity.getToken());
        map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

        return new Result().ok(map);
    }

    public ClientUserEntity atachUserInfo(ClientUserEntity user,AppLoginCheckVo vo){
        boolean updateFlag = false;
        String headIcon = vo.getHeadIcon();
        String nickName = vo.getNickName();

        if(headIcon != null){
            if(user.getHeadImg()== null){
                user.setHeadImg(headIcon);
                updateFlag = true;
            }
        }
        if(nickName != null){
            if(user.getUsername()==user.getMobile()||user.getUsername()==null){
                user.setUsername(nickName);
                updateFlag =true;
            }
        }
        if(updateFlag){
            updateFlag=false;
            clientUserDao.updateById(user);
        }
        return user;
    }
    @Override
    public Result appLoginCheck(AppLoginCheckVo vo) {
        String mobile = vo.getMobile();
        String unionid = vo.getUnionid();
        String headIcon = vo.getHeadIcon();
        String nickName = vo.getNickName();
        if(mobile != null){
            ClientUserEntity user = clientUserDao.getUserByMobile(mobile);
            if(user != null){
                return loginSuccess(atachUserInfo(user,vo));
            }else{
                //走注册流程
                return userRegister(vo);
            }
        }

        if(unionid != null){
            QueryWrapper<ClientUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionid",unionid);
            List<ClientUserEntity> users = clientUserDao.selectList(queryWrapper);
            if(users.size()>0){
                if(users.size()>1){
                    System.out.println("用户帐号："+users.get(0).toString()+"重复，请及时处理！");
                }
                ClientUserEntity u = users.get(0);
                return loginSuccess(atachUserInfo(u,vo));
            }else{
                return new Result().error("failure");
            }
        }
        return new Result().error("failure");
    }

}