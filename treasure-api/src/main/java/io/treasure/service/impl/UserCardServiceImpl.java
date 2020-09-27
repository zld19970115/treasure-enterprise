package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.dao.RecordGiftDao;
import io.treasure.dao.UserCardDao;
import io.treasure.dto.CardInfoDTO;
import io.treasure.dto.MulitCouponBoundleNewDto;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserCardServiceImpl extends CrudServiceImpl<UserCardDao, CardInfoEntity, CardInfoDTO> implements UserCardService {
    @Autowired
    private ClientUserServiceImpl clientUserService;
    @Autowired
    private RecordGiftServiceImpl recordGiftService;
    @Autowired
    private UserCardDao userCardDao;
    @Autowired
    private RecordGiftDao recordGiftDao;
    @Autowired
    private CouponForActivityService couponForActivityService;
    @Override
    public QueryWrapper<CardInfoEntity> getWrapper(Map<String, Object> params) {
        return null;
    }
    @Autowired
    private MulitCouponBoundleDao mulitCouponBoundleDao;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public  Result selectByIdAndPassword(long id, String password,long userId) {

        CardInfoEntity cardInfoEntity = baseDao.selectByIdAndPassword(id, password);
        if (cardInfoEntity==null){
            return new Result().error("账号密码错误");
        }
        if (cardInfoEntity.getStatus()==1){
            return new Result().error("该卡密未激活");
        }
        if (cardInfoEntity.getStatus()==3){
            return new Result().error("该卡密已使用");
        }
        if (cardInfoEntity.getStatus()==9){
            return new Result().error("该卡密已删除");
        }
        BigDecimal a = new BigDecimal("2000");

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("请登录");
          }
        if( clientUserEntity.getGift().compareTo(a)==1){
            return new Result().error("代付金余额大于2000不可充值");
        }
        BigDecimal money = cardInfoEntity.getMoney().add(clientUserEntity.getGift());
        clientUserEntity.setGift(money);
        clientUserService.updateById(clientUserEntity);

        Date date = new Date();
        cardInfoEntity.setStatus(3);
        cardInfoEntity.setBindCardDate(date);
        cardInfoEntity.setBindCardUser(userId);
        baseDao.updateById(cardInfoEntity);

        recordGiftService.insertRecordGift(userId,date,clientUserEntity.getGift(),cardInfoEntity.getMoney());

        return new Result().ok("充值成功");
    }

    @Override
    public Result selectMartCouponForBalance(long id, String password, long userId) throws ParseException {

        CardInfoEntity cardInfoEntity = baseDao.selectByIdAndPasswordandType(id, password);
        if (cardInfoEntity==null){
            return new Result().error("账号密码错误");
        }
        if (cardInfoEntity.getStatus()==1){
            return new Result().error("该卡密未激活");
        }
        if (cardInfoEntity.getStatus()==3){
            return new Result().error("该卡密已使用");
        }
        if (cardInfoEntity.getStatus()==9){
            return new Result().error("该卡密已删除");
        }

        BigDecimal a = new BigDecimal("200");

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("请登录");
        }
        BigDecimal clientActivityCoinsVolume = couponForActivityService.getClientActivityCoinsVolume(userId);
        if( clientActivityCoinsVolume.compareTo(a)==1){
            return new Result().error("有效期宝币余额大于200不可充值");
        }
        List<RecordGiftDTO> recordGiftDTOS = recordGiftDao.selectByUserIdandstatus(userId, 12);
        if (recordGiftDTOS.size()>=2){
            return new Result().error("充值已达到上限次数");
        }

 //       BigDecimal money = cardInfoEntity.getMoney().add(clientUserEntity.getBalance());
//        clientUserEntity.setBalance(money);
//        clientUserService.updateById(clientUserEntity);

        Date date = new Date();
        cardInfoEntity.setStatus(3);
        cardInfoEntity.setBindCardDate(date);
        cardInfoEntity.setBindCardUser(userId);
        baseDao.updateById(cardInfoEntity);

        couponForActivityService.insertClientActivityRecord(userId,cardInfoEntity.getMoney(),1,1, ESharingRewardGoods.ActityValidityUnit.UNIT_MONTHS);


      recordGiftService.insertRecordBalance(userId,date,clientUserEntity.getBalance(),cardInfoEntity.getMoney());

        return new Result().ok("充值成功");
    }

    @Override
    public Result getOneBalance(long userId) throws ParseException {
        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities = mulitCouponBoundleDao.selectByMothod(userId, 4);
        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities1 = mulitCouponBoundleDao.selectCOUNTByMothod(4);
        if (mulitCouponBoundleEntities.size()>0){
            return  new Result().error("您已经领取过了");
        }
        int i = mulitCouponBoundleDao.selectByrule(4);
        int i1 = mulitCouponBoundleDao.selectByrule1(4);
        if(mulitCouponBoundleEntities1.size()>=i1){
            return  new Result().error("人数超限");
        }
        BigDecimal a = new BigDecimal(i+"");
        couponForActivityService.insertClientActivityRecord(userId,a,4,1, ESharingRewardGoods.ActityValidityUnit.UNIT_MONTHS);
        return new Result().error("成功领取"+a+"宝币");
    }

    @Override
    public Result getOneBalance2(long userId) throws ParseException {
        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities = mulitCouponBoundleDao.selectByMothod(userId, 4);
        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities1 = mulitCouponBoundleDao.selectCOUNTByMothod(4);
        if (mulitCouponBoundleEntities.size()>0){
            return  new Result().error("您已经领取过了");
        }
        int i = mulitCouponBoundleDao.selectByrule(5);
        int i1 = mulitCouponBoundleDao.selectByrule1(5);
        if(mulitCouponBoundleEntities1.size()>=i1){
            return  new Result().error("人数超限");
        }
        BigDecimal a = new BigDecimal(i+"");
        couponForActivityService.insertClientActivityRecord(userId,a,4,1, ESharingRewardGoods.ActityValidityUnit.UNIT_MONTHS);
        return new Result().error("成功领取"+a+"宝币");
    }

    @Override
    public Result getOneBalance3(long userId) throws ParseException {
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        List<RecordGiftDTO> recordGiftDTOS = recordGiftDao.selectByUserIdandstatus(userId, 15);
        if (recordGiftDTOS.size()>=1){
            return new Result().error("充值已达到上限次数");
        }
        int i = mulitCouponBoundleDao.selectByrule(6);
        BigDecimal a = new BigDecimal(i+"");
        BigDecimal gift = clientUserEntity.getGift();
        BigDecimal newGift = gift.add(a);
        clientUserEntity.setGift(newGift);
        clientUserService.updateById(clientUserEntity);
        recordGiftDao.insertRecordbyGiftAdmin(userId,clientUserEntity.getMobile(),a,newGift,15,new Date(),userId);
        return new Result().error("成功领取"+a+"代付金");
    }


    @Override
    public PageData<CardInfoDTO> pageList(Map params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<CardInfoDTO> page = (Page) userCardDao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

    @Override
    public List<CardInfoDTO> selectByNoCode() {
        return baseDao.selectByNoCode();
    }

    @Override
    public Result openCard(List<Long> ids,Long userId) {
        return new Result().ok(userCardDao.openCard(ids,userId));
    }

    @Override
    public void updateCode(String s, long id) {
        baseDao.updateCode(s,id);
    }

}
