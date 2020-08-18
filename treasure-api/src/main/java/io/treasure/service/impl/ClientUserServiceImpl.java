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
    public Map<String, Object> login(LoginDTO dto) {
        ClientUserEntity user = getByMobile(dto.getMobile());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);

        //密码错误
        if(!user.getPassword().equals(DigestUtils.sha256Hex(dto.getPassword()))){
            throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
//        if(user.getStatus()==9){
//            throw new RenException(14000,"用户已注销");
//        }
        //获取登录token
        TokenEntity tokenEntity = tokenService.createToken(user.getId());

        Map<String, Object> map = new HashMap<>(2);
        map.put("user",user);
        map.put("token", tokenEntity.getToken());
        map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

        return map;
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

}