package io.treasure.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.ActivityDao;
import io.treasure.dto.ActivityDto;
import io.treasure.dto.ActivityRartakeDto;
import io.treasure.dto.ReceiveGiftDto;
import io.treasure.dto.UpdateHotDto;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.ActivityGiveEntity;
import io.treasure.entity.ActivityGiveLogEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ActivityService;
import io.treasure.service.ClientUserService;
import io.treasure.service.RecordGiftService;
import io.treasure.service.TokenService;
import io.treasure.utils.DateUtil;
import io.treasure.vo.ActivityRartakeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RecordGiftService recordGiftService;

    @Autowired
    private ClientUserService clientUserService;

    @Override
    public PageData<ActivityEntity> activityPage(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<ActivityEntity> page = (Page) activityDao.activityPage(params);
        return new PageData<ActivityEntity>(page.getResult(),page.getTotal());
    }

    @Override
    public int del(Long id) {
        return activityDao.del(id);
    }

    @Override
    public int update(ActivityDto dto) {
        ActivityEntity obj = new ActivityEntity();
        BeanUtils.copyProperties(dto,obj);
        obj.setUpdateDate(new Date());
        if(activityDao.update(obj) > 0) {
            return 200;
        }
        return 0;
    }

    @Override
    public int insert(ActivityDto dto) {
        ActivityEntity obj = new ActivityEntity();
        BeanUtils.copyProperties(dto,obj);
        obj.setCreateDate(new Date());
        if(activityDao.insert(obj) > 0) {
            if (dto.getType() == 1) {
                ActivityGiveEntity giveObj = new ActivityGiveEntity();
                giveObj.setActivityId(obj.getId());
                giveObj.setType(obj.getType());
                giveObj.setInitNum(dto.getInitNum());
                giveObj.setTotalCost(dto.getTotalCost());
                giveObj.setReceiveNum(0);
                giveObj.setCost(dto.getTotalCost().divide(new BigDecimal(dto.getInitNum()),2, BigDecimal.ROUND_HALF_DOWN));
                giveObj.setCreateDate(new Date());
                if(activityDao.insertGive(giveObj) > 0) {
                    return 200;
                }
            }
        }
        return 0;
    }

    @Override
    public int updateGive(ActivityGiveEntity dto) {
        return activityDao.updateGive(dto);
    }

    @Override
    public int insertGive(ActivityGiveEntity dto) {
        return activityDao.insertGive(dto);
    }

    @Override
    public int insertGiveLog(ActivityGiveLogEntity dto) {
        return activityDao.insertGiveLog(dto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int receiveGift(ReceiveGiftDto dto) {
        if(dto.getActivityId() == null || dto.getToken() == null) {
            return 0;
        }
        TokenEntity obj = tokenService.getByToken(dto.getToken());
        if(obj == null || obj.getUserId() == null) {
            return 1;
        }
        ActivityDto activityDto = selectById(dto.getActivityId());
        if(activityDto == null) {
            return 0;
        }
        try {
            if(activityDto.getState() == 0 || (DateUtil.strToDate(activityDto.getStatrDate()).getTime() > new Date().getTime())) {
                return 2;
            }
            if(DateUtil.strToDate(activityDto.getEndDate()).getTime() < new Date().getTime()) {
                return 3;
            }
        } catch (ParseException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        if(activityDao.cancellationUser(dto.getActivityId(), clientUserService.get(obj.getUserId()).getMobile()) > 0) {
            return 5;
        }
        if(activityDao.receiveGift(dto.getActivityId(),obj.getUserId()) > 0) {
            ActivityGiveEntity activityGiveEntity = selectGiveByActivityId(dto.getActivityId());
            ActivityGiveLogEntity logObj = new ActivityGiveLogEntity();
            logObj.setActivityId(dto.getActivityId());
            logObj.setGiveId(activityGiveEntity.getId());
            logObj.setUserId(obj.getUserId());
            logObj.setType(activityDto.getType());
            logObj.setCost(activityGiveEntity.getCost());
            logObj.setCreateDate(new Date());
            if(activityDao.insertGiveLog(logObj) > 0) {
                BigDecimal gift = clientUserService.getClientUser(obj.getUserId()).getGift();
                clientUserService.addRecordGiftByUserid(obj.getUserId().toString(),activityGiveEntity.getCost().toString());
                recordGiftService.insertRecordGift6(obj.getUserId(), new Date(),gift.add(activityGiveEntity.getCost()) , activityGiveEntity.getCost());
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return 200;
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return 4;
    }

    @Override
    public ActivityDto selectById(Long id) {
        ActivityDto dto = new ActivityDto();
        ActivityEntity obj = activityDao.selectById(id);
        if(obj == null) {
            return null;
        }
        BeanUtils.copyProperties(obj, dto);
        if(obj.getType() == 1) {
            ActivityGiveEntity giveObj = selectGiveByActivityId(obj.getId());
            dto.setInitNum(giveObj.getInitNum());
            dto.setTotalCost(giveObj.getTotalCost());
        }
        return dto;
    }

    @Override
    public ActivityGiveEntity selectGiveByActivityId(Long activityId) {
        return activityDao.selectGiveById(activityId);
    }

    @Override
    public Result<ActivityRartakeVo> activityRartake(ActivityRartakeDto dto) {
        ActivityRartakeVo vo = new ActivityRartakeVo();
        if(dto.getToken() == null) {
            return new Result<ActivityRartakeVo>().error("请登录");
        }
        if(dto.getActivityId() == null) {
            return new Result<ActivityRartakeVo>().error("活动无效");
        }
        TokenEntity obj = tokenService.getByToken(dto.getToken());
        if(obj == null || obj.getUserId() == null) {
            return new Result<ActivityRartakeVo>().error("token失效");
        }
        ActivityDto activityDto = selectById(dto.getActivityId());
        if(activityDto == null) {
            return new Result<ActivityRartakeVo>().error("活动无效");
        }
        try {
            if(activityDto.getState() == 0 || (DateUtil.strToDate(activityDto.getStatrDate()).getTime() > new Date().getTime())) {
                return new Result<ActivityRartakeVo>().error("活动未开始");
            }
            if(DateUtil.strToDate(activityDto.getEndDate()).getTime() < new Date().getTime()) {
                return new Result<ActivityRartakeVo>().error("活动以结束");
            }
        } catch (ParseException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        vo.setGift(selectGiveByActivityId(dto.getActivityId()).getCost());
        if(activityDao.cancellationUser(dto.getActivityId(), clientUserService.get(obj.getUserId()).getMobile()) > 0) {
            vo.setState(1);
            return new Result<ActivityRartakeVo>().ok(vo);
        }
        int count = activityDao.activityRartake(dto.getActivityId(),obj.getUserId());
        if(count == 0) {
            vo.setState(0);
            return new Result<ActivityRartakeVo>().ok(vo);
        }
        vo.setState(1);
        return new Result<ActivityRartakeVo>().ok(vo);
    }

    @Override
    public Result<ActivityRartakeVo> hot(String token) {
        ActivityRartakeVo vo = new ActivityRartakeVo();
        if (token == null) {
            return new Result<ActivityRartakeVo>().error("请登录");
        }
        TokenEntity obj = tokenService.getByToken(token);
        if (obj == null || obj.getUserId() == null) {
            return new Result<ActivityRartakeVo>().error("token失效");
        }
        ActivityEntity dto = activityDao.getHotActivity();
        if (dto != null) {
            vo.setId(dto.getId());
            vo.setGift(selectGiveByActivityId(dto.getId()).getCost());
            if(activityDao.cancellationUser(dto.getId(), clientUserService.get(obj.getUserId()).getMobile()) > 0) {
                vo.setState(1);
                return new Result<ActivityRartakeVo>().ok(vo);
            }
            int count = activityDao.activityRartake(dto.getId(),obj.getUserId());
            if(count == 0) {
                vo.setState(0);
                return new Result<ActivityRartakeVo>().ok(vo);
            }
            vo.setState(1);
        }
        return new Result<ActivityRartakeVo>().ok(vo);
    }

    @Override
    public Result<String> updateHot(UpdateHotDto dto) {
        ActivityEntity obj = activityDao.getHotActivity();
        ActivityDto e = new ActivityDto();
        e.setHot(dto.getHot());
        e.setId(dto.getId());
        update(e);
        if(obj != null && obj.getId() != dto.getId()) {
            e.setHot(0);
            e.setId(obj.getId());
            update(e);
        }
        return new Result<String>().ok("");
    }

}
