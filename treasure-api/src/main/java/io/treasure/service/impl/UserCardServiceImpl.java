package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.UserCardDao;
import io.treasure.dto.CardInfoDTO;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Override
    public QueryWrapper<CardInfoEntity> getWrapper(Map<String, Object> params) {
        return null;
    }


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
        BigDecimal a = new BigDecimal("500");

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("请登录");
          }
        if( clientUserEntity.getGift().compareTo(a)==1){
            return new Result().error("代付金余额大于500不可充值");
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
    public PageData<CardInfoDTO> pageList(Map params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<CardInfoDTO> page = (Page) userCardDao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

    @Override
    public Result openCard(List<Long> ids,Long userId) {
        return new Result().ok(userCardDao.openCard(ids,userId));
    }

}
