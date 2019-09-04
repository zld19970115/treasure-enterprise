
package io.treasure.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.enm.Constants;
import io.treasure.entity.*;
import io.treasure.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import io.treasure.utils.OrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private ClientUserService clientUserService;

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
    public Result orderSave(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user) {
        Result result=new Result();
        //生成订单号
        String orderId= OrderUtil.getOrderIdByTime(dto.getId());
        //锁定包房/散台
         MerchantRoomParamsSetEntity merchantRoomParamsSetEntity=merchantRoomParamsSetService.selectById(dto.getReservationId());
        if(merchantRoomParamsSetEntity==null){
            return result.error(-5,"没有此包房/散台");
        }
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
         //是否使用赠送金
        if(dto.getGiftMoney().doubleValue()>0)
        {
            ClientUserEntity clientUserEntity=clientUserService.selectById(user.getId());
            BigDecimal gift=clientUserEntity.getGift();
            BigDecimal useGift=new BigDecimal(dto.getGiftMoney().doubleValue()).setScale(2);
            if(gift.compareTo(useGift)==-1){
                return result.error(-7,"您的赠送金不足！");
            }else{
                clientUserEntity.setGift(gift.subtract(useGift));
            }
            clientUserService.updateById(clientUserEntity);
        }
        //保存主订单
        MasterOrderEntity masterOrderEntity=ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setStatus(Constants.OrderStatus.NOPAYORDER.getValue());
        masterOrderEntity.setInvoice("0");
        int i=baseDao.insert(masterOrderEntity);
        if(i<=0){
            return result.error(-2,"没有订单数据！");
        }
        //保存订单菜品
        if(masterOrderEntity.getReservationId()!=Constants.ReservationType.ONLYROOMRESERVATION.getValue()){
            if(dtoList==null)
            {
                return result.error(-6,"没有菜品数据！");
            }
            dtoList.forEach(slaveOrderEntity -> {
                slaveOrderEntity.setOrderId(orderId);
                slaveOrderEntity.setStatus(1);
            });
//        List<SlaveOrderEntity> slaveOrderEntityList=ConvertUtils.sourceToTarget(dtoList,SlaveOrderEntity.class);
            boolean b=slaveOrderService.insertBatch(dtoList);
            if(!b){
                return result.error(-3,"没有订单菜品数据！");
            }
        }
        return result.ok(orderId);
    }

    @Override
    public PageData<MasterOrderDTO> listPage(Map<String, Object> params) {
        IPage<MasterOrderEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                getQueryWrapper(params)
        );
        return getPageData(page, MasterOrderDTO.class);
    }

    @Override
    public Result updateByCheck(Long id) {
        Result result=new Result();
        MasterOrderEntity masterOrderEntity=baseDao.selectById(id);
        int s=masterOrderEntity.getStatus();
        if(s!=4){
            return result.error(-1,"不是未支付订单,不能取消订单！");
        }
        masterOrderEntity.setCheckStatus(1);
        masterOrderEntity.setCheckMode(Constants.CheckMode.USERCHECK.getValue());
        int i=baseDao.updateById(masterOrderEntity);
        if(i>0){
            result.ok(true);
        }else{
            return result.error(-1,"结账失败！请联系商家！");
        }
        return result;
    }

    @Override
    public Result updateByCancel(Map<String, Object> params) {
        Result result=new Result();
        MasterOrderEntity masterOrderEntity=baseDao.selectById(Convert.toLong(params.get("id")));
        int s=masterOrderEntity.getStatus();
        if(s!=1){
            return result.error(-1,"不是未支付订单,不能取消订单！");
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.CANCELNOPAYORDER.getValue());
        String refundReason=(String)params.get("refundReason");
        if(StringUtils.isNotBlank(refundReason)){
            masterOrderEntity.setRefundReason(refundReason);
        }
        int i=baseDao.updateById(masterOrderEntity);
        if(i>0){
            result.ok(true);
        }else{
            return result.error(-1,"取消订单失败！");
        }
        return result;

    }

    @Override
    public Result updateByApplyRefund(Map<String, Object> params) {
        Result result=new Result();
        MasterOrderEntity masterOrderEntity=baseDao.selectById(Convert.toLong(params.get("id")));
        int s=masterOrderEntity.getStatus();
        if(s!=4&&s!=2){
            return result.error(-1,"订单不能申请退款！");
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue());
        String refundReason=(String)params.get("refundReason");
        if(StringUtils.isNotBlank(refundReason)){
            masterOrderEntity.setRefundReason(refundReason);
        }
        int i=baseDao.updateById(masterOrderEntity);
        if(i>0){
            result.ok(true);
        }else{
            return result.error(-1,"申请退款失败！");
        }
        return result;
    }

    @Override
    public Map<String, String> getNotify(Constants.PayMode alipay, BigDecimal bigDecimal, String out_trade_no) {

        return null;
    }

    private Wrapper<MasterOrderEntity> getQueryWrapper(Map<String, Object> params) {
        String userId = (String)params.get("userId");
        //状态
        String status=(String)params.get("status");
        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(userId), "creator", userId);
        if(StringUtils.isNotBlank(status) && status.indexOf(",")>-1){
            wrapper.in(StringUtils.isNotBlank(status),"status",status);
        }else{
            wrapper.eq(StringUtils.isNotBlank(status), "status",status);
        }
        return wrapper;
    }

}