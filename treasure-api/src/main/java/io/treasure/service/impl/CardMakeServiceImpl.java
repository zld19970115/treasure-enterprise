package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.CardMakeDao;
import io.treasure.dao.UserCardDao;
import io.treasure.dto.CardMakeDTO;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.CardMakeEntity;
import io.treasure.service.CardMakeService;
import io.treasure.service.UserCardService;
import io.treasure.utils.RandomPwd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CardMakeServiceImpl extends CrudServiceImpl<CardMakeDao, CardMakeEntity, CardMakeDTO> implements CardMakeService {

    @Autowired
    private CardMakeDao dao;

    @Autowired
    private UserCardService userCardService;

    @Override
    public QueryWrapper<CardMakeEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageData<CardMakeDTO> pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<CardMakeDTO> page = (Page) dao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result makeCard(CardMakeEntity dto) {
        if(dto.getCardNum() == null || dto.getCardMoney() == null) {
            return new Result<>().error("参数不正确");
        }
        int batch = dao.queryMax();
        dto.setCreateDate(new Date());
        dto.setCardBatch(batch);
        if(dao.insert(dto) > 0) {
            List<CardInfoEntity> list =new ArrayList<>();
            for(int n = 0;n < dto.getCardNum();n++){
                CardInfoEntity info = new CardInfoEntity();
                info.setBatch(batch);
                info.setMoney(dto.getCardMoney());
                info.setStatus(1);
                info.setType(dto.getCardType());
                info.setPassword(RandomPwd.getRandomPwd(16));
                list.add(info);
            }
            userCardService.insertBatch(list);
        }
        return new Result<>().ok("操作成功");
    }

}
