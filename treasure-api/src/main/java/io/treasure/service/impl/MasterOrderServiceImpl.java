
package io.treasure.service.impl;

import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.*;
import io.treasure.service.MasterOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import io.treasure.service.MerchantRoomParamsSetService;
import io.treasure.service.MerchantService;
import io.treasure.service.SlaveOrderService;
import io.treasure.utils.OrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class MasterOrderServiceImpl extends CrudServiceImpl<MasterOrderDao, MasterOrderEntity, MasterOrderDTO> implements MasterOrderService {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SlaveOrderService slaveOrderService;
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;

    @Override
    public QueryWrapper<MasterOrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        if(StringUtils.isNotBlank(status) && status.indexOf(",")>-1){
            wrapper.in(StringUtils.isNotBlank(status),"pay_status",status);
        }else{
            wrapper.eq(StringUtils.isNotBlank(status), "pay_status",status);
        }
        return wrapper;
    }


    @Override
    public void updateStatusAndReason(long id, int status, long verify, Date verify_date, String verify_reason) {
        baseDao.updateStatusAndReason(id,status,verify,verify_date,verify_reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO getOrder(String orderId) {
        MasterOrderEntity masterOrderEntity=baseDao.selectByOrderId(orderId);
        OrderDTO orderDTO=ConvertUtils.sourceToTarget(masterOrderEntity,OrderDTO.class);
        MerchantEntity merchantEntity=merchantService.selectById(masterOrderEntity.getMerchantId());
        orderDTO.setMerchantInfo(merchantEntity);
        List<SlaveOrderEntity> slaveOrderEntitys=slaveOrderService.selectByOrderId(orderId);
        orderDTO.setSlaveOrder(slaveOrderEntitys);
        MerchantRoomParamsSetEntity merchantRoomParamsSetEntity=merchantRoomParamsSetService.selectById(masterOrderEntity.getReservationId());
        orderDTO.setReservationInfo(merchantRoomParamsSetEntity);
        return orderDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result orderSave(MasterOrderDTO dto, List<SlaveOrderDTO> dtoList, ClientUserEntity user) {
        Result result=new Result();
        //生成订单号
        String orderId= OrderUtil.getOrderIdByTime(user.getId());
        //锁定包房/散台
         MerchantRoomParamsSetEntity merchantRoomParamsSetEntity=merchantRoomParamsSetService.selectById(dto.getReservationId());
         int isUse=merchantRoomParamsSetEntity.getState();
         if(isUse==0){
             merchantRoomParamsSetEntity.setState(1);
             boolean bb=merchantRoomParamsSetService.updateById(merchantRoomParamsSetEntity);
             if(!bb){
                 return result.error(-4,"包房/散台预定出错！");
             }
         }else if(isUse==1){
             return result.error(-1,"包房/散台已经预定,请重新选择！");
         }
        //保存主订单
        MasterOrderEntity masterOrderEntity=ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setStatus(1);
        masterOrderEntity.setInvoice("0");
        masterOrderEntity.setCheckStatus(0);
        int i=baseDao.insert(masterOrderEntity);
        if(i<=0){
            return result.error(-2,"没有订单数据！");
        }
        //保存订单菜品
        dtoList.forEach(slaveOrderDTO -> {
            slaveOrderDTO.setOrderId(orderId);
            slaveOrderDTO.setStatus(1);
        });
        List<SlaveOrderEntity> slaveOrderEntityList=ConvertUtils.sourceToTarget(dtoList,SlaveOrderEntity.class);
        boolean b=slaveOrderService.insertBatch(slaveOrderEntityList);
        if(!b){
            return result.error(-3,"没有订单菜品数据！");
        }
        return result;
    }

}