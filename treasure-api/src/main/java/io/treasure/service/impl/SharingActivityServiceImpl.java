package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dao.SharingInitiatorDao;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.service.SharingActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SharingActivityServiceImpl implements SharingActivityService {

    @Autowired(required = false)
    private SharingActivityDao sharingActivityDao;

    /**
     * 检查活动是否有效，不存在或过期均无效
     * @return
     */
    @Override
    public boolean isEnableActivityById(Integer said){

        SharingActivityEntity saEntity = getOneById(said,true);
        if(saEntity != null)
            return true;
        return false;
    }

    /**
     * 根据活动取得活动详情
     * @param said
     * @param enableOnly
     * @return
     */
    @Override
    public SharingActivityEntity getOneById(Integer said,boolean enableOnly){

        QueryWrapper<SharingActivityEntity> saeqw = new QueryWrapper<>();

        saeqw.eq("sa_id",said);
        if (enableOnly) {
            Date now = new Date();
            saeqw.lt("open_date", now);
            saeqw.gt("close_date", now);
        }
        return sharingActivityDao.selectOne(saeqw);
    }

    /**
     * 取得列表，根据活动有效性
     * @param enableOnly
     * @return
     */
    @Override
    public List<SharingActivityEntity> getList(boolean enableOnly){

        QueryWrapper<SharingActivityEntity> saeqw = new QueryWrapper<>();

        if (enableOnly) {
            Date now = new Date();
            saeqw.lt("open_date", now);
            saeqw.gt("close_date", now);
        }
        return sharingActivityDao.selectList(saeqw);
    }

    @Override
    public List<SharingActivityEntity> getListByMerchantIdAndStatus(long MerchantId,Date now) {
        Date d = now==null?new Date():now;
        return sharingActivityDao.getListByMerchantIdAndStatus(MerchantId,d);
    }

    @Override
    public void insertOne(SharingActivityEntity sharingActivityEntity){
        sharingActivityDao.insert(sharingActivityEntity);
    }
    @Override
    public void updateOne(SharingActivityEntity sharingActivityEntity){

        sharingActivityDao.updateById(sharingActivityEntity);
    }


}
