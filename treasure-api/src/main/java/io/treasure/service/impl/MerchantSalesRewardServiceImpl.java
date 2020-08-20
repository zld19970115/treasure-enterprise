package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.treasure.common.exception.RenException;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantSalesRewardDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.service.MerchantSalesRewardService;
import io.treasure.utils.AdressIPUtil;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;
import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MerchantSalesRewardServiceImpl implements MerchantSalesRewardService {

    @Autowired(required = false)
    private MerchantSalesRewardDao merchantSalesRewardDao;
    @Autowired(required = false)
    private MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;

    @Override
    public MerchantSalesRewardEntity getParams(){
        List<MerchantSalesRewardEntity> merchantSalesRewardEntities = merchantSalesRewardDao.selectList(null);
        if(merchantSalesRewardEntities.size()>0)
            return  merchantSalesRewardEntities.get(0);
        return null;
    }

    @Override
    public void setParams(MerchantSalesRewardEntity entity){
        merchantSalesRewardDao.updateById(entity);
    }

    @Override
    public void insertOne(MerchantSalesRewardEntity entity) {
        if (getParams() == null) {
            merchantSalesRewardDao.insert(entity);
        }
    }

    @Override
    public IPage<MerchantSalesRewardRecordEntity> getRecords(MerchantSalesRewardRecordVo vo){
        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        MerchantSalesRewardRecordEntity queryEntity = vo.getMerchantSalesRewardRecordEntity();
        if(queryEntity != null){

            if(queryEntity.getId() != null)
                queryWrapper.eq("id",queryEntity.getId());
            if(queryEntity.getMId() != null)
                queryWrapper.eq("m_id",queryEntity.getMId());
            if(queryEntity.getOutline() != null)
                queryWrapper.like("outline",queryEntity.getOutline());
            if(queryEntity.getRewardType() != null)
                queryWrapper.eq("reward_type",queryEntity.getRewardType());
            if(queryEntity.getRewardValue() != null && vo.getMinValue()==null)
                queryWrapper.eq("reward_value",queryEntity.getRewardValue());//大于等于
        }

        if(vo.getMinValue() != null)
            queryWrapper.le("reward_value",vo.getMinValue());//须测
//        if(vo.getStartTime() != null)
//            queryWrapper.ge("create_pmt",vo.getStartTime());
//        if(vo.getStopTime() != null)
//            queryWrapper.le("create_pmt",vo.getStopTime());

        Page<MerchantSalesRewardRecordEntity> map = new Page<MerchantSalesRewardRecordEntity>(vo.getIndex(),vo.getPagesNum());
        IPage<MerchantSalesRewardRecordEntity> pages = merchantSalesRewardRecordDao.selectPage(map,queryWrapper);
        return pages;
    }

    public List<RewardMchList> getRewardMchList(MerchantSalesRewardRecordVo vo){

        List<RewardMchList> list = merchantSalesRewardRecordDao.reward_mch_list(vo);
        return list;
    }

    public List<RewardMchList> getRewardMchList(List<Long> mIds){
        List<RewardMchList> rewardMchList = merchantSalesRewardRecordDao.getRewardMchList(mIds);
        return rewardMchList;
    }

    @Override
    public void delRecord(Long id){
        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectById(id);
        if(entity != null){
            if(entity.getCashOutStatus()== 2){//2表示已提现
                merchantSalesRewardRecordDao.deleteById(id);
            }
        }
    }

    @Override
    public void insertRecord(MerchantSalesRewardRecordEntity entity){
        merchantSalesRewardRecordDao.insert(entity);
    }

    //此功能目前不完整，暂时先不要用
    @Override
    public void insertBatchRecords(List<RewardMchList> list){
        //List<MerchantSalesRewardRecordEntity> queryList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            MerchantSalesRewardRecordEntity targetItem = new MerchantSalesRewardRecordEntity();
            RewardMchList rewardMchList = list.get(0);

            targetItem.setMId(rewardMchList.getMerchantId());
            String salesVolume = rewardMchList.getSalesVolume();
            String s = new BigDecimal(salesVolume).setScale(0, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            targetItem.setPreferenceVolume(Integer.parseInt(s));

            String merchantProceed = new BigDecimal(rewardMchList.getMerchantProceed()).setScale(0, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();

            //重新获取需要要反现的记录列表

            targetItem.setRewardValue(Integer.parseInt(merchantProceed));

            targetItem.setCreatePmt(new Date());
            targetItem.setOutline("销售返利");
            targetItem.setRewardType(1);
            //queryList.add(targetItem);
            merchantSalesRewardRecordDao.insert(targetItem);
        }
    }

    public int updateWithDrawStatusById(List<MerchantSalesRewardRecordEntity> entites,Integer method){
        for(int i=0;i<entites.size();i++){
            MerchantSalesRewardRecordEntity entity = entites.get(i);
            entity.setMethod(method);
            entity.setCashOutStatus(2);
            merchantSalesRewardRecordDao.updateById(entity);
        }
        return 1;
    }

    public String getNotWithdrawRewardAmount(Long mchId){

        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(reward_value) as reward_value");
        queryWrapper.eq("m_id",mchId);
        queryWrapper.eq("cash_out_status",1);

        MerchantSalesRewardRecordEntity entity = merchantSalesRewardRecordDao.selectOne(queryWrapper);
        if(entity != null){
            return entity.getRewardValue().toString();
        }
        return "0";
    }
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Result audit(UserWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException {
//        Result result=new Result();
//        Date date = new Date();
//        int state=dto.getVerifyState();
//        if(state==1){
//            throw new RenException("审核状态不正确！！！");
//        }else{
//            dto.setVerifyDate(date);
//        }
//        if(state==2){
//            int type=dto.getType();
//            String orderNumber=dto.getId().toString();
//            Long userId=dto.getUserId();
//            String amount=dto.getMoney().toString();
//            String ipAddress= AdressIPUtil.getClientIpAddress(request);
//            if(type==1){
//                result=this.wxWithdraw(orderNumber,userId,amount,ipAddress,dto);
//            }else if(type==2){
//                result=this.aliWithdraw(orderNumber,userId,amount,dto);
//            }
//        } else {
//
//            ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getUserId());
//            BigDecimal coin = clientUserEntity.getCoin();
//            java.math.BigDecimal bd1 = new java.math.BigDecimal( dto.getMoney());
//            coin = coin.add(bd1);
//            clientUserEntity.setCoin(coin);
//            clientUserService.updateById(clientUserEntity);
//            this.update(dto);
//        }
//        return result;
//    }

}
