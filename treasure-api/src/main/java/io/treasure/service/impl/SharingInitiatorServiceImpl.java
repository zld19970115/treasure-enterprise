package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dao.SharingInitiatorDao;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingInitiatorEntity;
import io.treasure.service.SharingInitiatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SharingInitiatorServiceImpl implements SharingInitiatorService {

    @Autowired(required = false)
    private SharingInitiatorDao sharingInitiatorDao;


    /**
     * 检查活动是否有效，不存在或过期均无效,
     * @return
     */
    public boolean isExistByInitiatorId(Long intitiatorId){

        SharingInitiatorEntity siEntity = getOneByInitiatorId(intitiatorId,null);
        if(siEntity != null)
            return true;
        return false;
    }

    /**
     * 检查是否已经参加过本编号的活动了
     * @param intitiatorId
     * @param saId
     * @return
     */
    public boolean isParticipated(Long intitiatorId,Integer saId){

        SharingInitiatorEntity siEntity = getOneByInitiatorId(intitiatorId,saId);
        if(siEntity != null)
            return true;
        return false;
    }


    /**
     * 根据活动取得活动详情,或者查询某人是否参加过某活动了
     * @param intitiatorId:client_id /saId：活动编号
     * @return
     */
    public SharingInitiatorEntity getOneByInitiatorId(Long intitiatorId,Integer saId){

        QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();

        sieqw.eq("initiator_id",intitiatorId);
        if(saId != null)
            sieqw.eq("sa_id",saId);

        return sharingInitiatorDao.selectOne(sieqw);
    }

    /**
     * 取得订单位列表，根据活动有效性
     * @param result 1:只查已经成功的,0查询未成功的,null查询所有
     * @return
     */
    public List<SharingInitiatorEntity> getList(Integer result){

        if(result != null){
            QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();
            sieqw.eq("result",result);

            return sharingInitiatorDao.selectList(sieqw);
        }
        return sharingInitiatorDao.selectList(null);
    }

    /**
     * 修改个人助力状态
     * @param sharingInitiatorEntity
     */
    public boolean updateOneByInitiatorId(SharingInitiatorEntity sharingInitiatorEntity){
        Long id = sharingInitiatorEntity.getId();
        if(id == null){
            QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();
            sieqw.eq("id",id);
            SharingInitiatorEntity sourceRecord = sharingInitiatorDao.selectOne(sieqw);

            if(sourceRecord != null){
                Long id1 = sourceRecord.getId();
                sharingInitiatorEntity.setId(id1);
                sharingInitiatorDao.updateById(sharingInitiatorEntity);
                return true;//更新完成
            }else{
                return false;//更新失败
            }

        }

        sharingInitiatorDao.updateById(sharingInitiatorEntity);
        return true;

    }

    /**
     * 发起活动时处理
     * @param sharingInitiatorEntity
     * @return
     */
    public boolean insertOne(SharingInitiatorEntity sharingInitiatorEntity){
        if(isParticipated(sharingInitiatorEntity.getInitiatorId(),sharingInitiatorEntity.getSaId()))
            return false;//已经参加过此活动了

        sharingInitiatorDao.insert(sharingInitiatorEntity);
        return true;//成功参加了本活动
    }


}
