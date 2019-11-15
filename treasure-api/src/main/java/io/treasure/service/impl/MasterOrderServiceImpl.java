
package io.treasure.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.*;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import io.treasure.utils.OrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
    private MerchantRoomService merchantRoomService;
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;
    @Autowired
    private ClientUserService clientUserService;

    @Autowired
    private GoodService goodService;
    @Autowired
    private StimmeService stimmeService;
    @Autowired
    private MasterOrderService masterOrderService;

    @Autowired
    private MerchantCouponService merchantCouponService;

    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;
    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private MerchantUserService merchantUserService;

    @Override
    public QueryWrapper<MasterOrderEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");
        //状态
        String status = (String) params.get("status");
        //商户
        String merchantId = (String) params.get("merchantId");
        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        if (StringUtils.isNotBlank(status) && status.indexOf(",") > -1) {
            wrapper.in(StringUtils.isNotBlank(status), "pay_status", status);
        } else {
            wrapper.eq(StringUtils.isNotBlank(status), "pay_status", status);
        }
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        return wrapper;
    }

    /**
     * 取消订单
     *
     * @param id
     * @param status
     * @param verify
     * @param verify_date
     * @param refundReason
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result caleclUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.NOPAYORDER.getValue() || dto.getStatus() == Constants.OrderStatus.PAYORDER.getValue()) {
                baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
                }
                //退款
                Result result1 = payService.refundByOrder(dto.getOrderId(), dto.getPayMoney().toString());
                if (result1.success()) {
                    boolean b = (boolean) result1.getData();
                    if (!b) {
                        return new Result().error("支付失败！");
                    }
                } else {
                    return new Result().error(result1.getMsg());
                }
                if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                    //同时将包房或者桌设置成未使用状态
                    merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                }
                ClientUserDTO userDto = clientUserService.get(dto.getCreator());
                if (null != userDto) {
                    String clientId = userDto.getClientId();
                    if (StringUtils.isNotBlank(clientId)) {
                        //发送个推消息
                        AppPushUtil.pushToSingleClient("商家拒绝接单", "您的订单商家已拒绝", "", clientId);
                    }
                }
            } else {
                return new Result().error("不能取消订单！");
            }
        } else {
            return new Result().error("无法获取订单信息！");
        }
        return new Result();
    }

    /**
     * 接受订单
     *
     * @param id
     * @param status
     * @param verify
     * @param verify_date
     * @param refundReason
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result acceptUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.PAYORDER.getValue()) {
                baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
                }
                ClientUserDTO userDto = clientUserService.get(dto.getCreator());
                if (null != userDto) {
                    String clientId = userDto.getClientId();
                    if (StringUtils.isNotBlank(clientId)) {
                        //发送个推消息
                        AppPushUtil.pushToSingleClient("订单管理", "商家已接单", "", clientId);
                    }
                }
            } else {
                return new Result().error("无法接受订单！");
            }
        } else {
            return new Result().error("无法获取订单！");
        }
        return new Result().ok("接受订单成功！");
    }

    /**
     * 商家翻台
     *
     * @param id
     * @param status
     * @param verify
     * @param verify_date
     * @param refundReason
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result finishUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue() || dto.getStatus() == Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()) {
                if (null == dto.getReservationId()) {
                    MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(dto.getOrderId());
                    if(roomOrderByPorderId!=null){
                        //同时将包房或者桌设置成未使用状态
                        merchantRoomParamsSetService.updateStatus(roomOrderByPorderId.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                    }

                }
                MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
                masterOrderEntity.setStatus(status);
                masterOrderEntity.setCheckStatus(1);
                masterOrderEntity.setCheckMode(Constants.CheckMode.MERCHANTCHECK.getValue());
                baseDao.updateStatusById(id, status, verify, verify_date, refundReason);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
                }
            } else if(dto.getStatus() == Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()){
                List<MasterOrderEntity> auxiliaryPayOrders = baseDao.getAuxiliaryPayOrders(dto.getOrderId());
                for (MasterOrderEntity s:auxiliaryPayOrders) {
                    if(s.getReservationType()==Constants.ReservationType.ONLYGOODRESERVATION.getValue()){
                        baseDao.updateOrderStatus(Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue(),s.getOrderId());
                        List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
                        for (SlaveOrderEntity soe:orderGoods) {
                            slaveOrderService.updateSlaveOrderStatus(Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue(),soe.getOrderId(),soe.getGoodId());
                        }
                    }
                    if(s.getReservationType()==Constants.ReservationType.ONLYROOMRESERVATION.getValue()){
                        merchantRoomParamsSetService.updateStatus(s.getReservationId(),0);
                        baseDao.updateOrderStatus(Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue(),s.getOrderId());
                    }
                }
            }else {
                return new Result().error("无法翻台订单！");
            }
        } else {
            return new Result().error("无法获取订单！");
        }
        return new Result().ok("订单翻台成功！");
    }

    /**
     * 同意退单
     *
     * @param id
     * @param status
     * @param verify
     * @param verify_date
     * @param refundReason
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    public Result refundYesUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        ClientUserDTO clientUserDTO = clientUserService.get(dto.getCreator());
        String clientId = clientUserDTO.getClientId();
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
                OrderDTO order = masterOrderService.getOrder(dto.getOrderId());
                if(order.getPOrderId().equals("0")&&order.getReservationType()==Constants.ReservationType.ONLYGOODRESERVATION.getValue()){
                    List<MasterOrderEntity> orderByPOrderId = masterOrderService.getOrderByPOrderId(order.getOrderId());
                    for (MasterOrderEntity s:orderByPOrderId) {
                        if(s.getPOrderId().equals(dto.getOrderId())&&s.getReservationType()==2){
                            merchantRoomParamsSetService.updateStatus(s.getReservationId(),0);
                        }
                    }
                }


                baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                    //同时将包房或者桌设置成未使用状态
                    merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                }
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
                }
                //退款
                Result result1 = payService.refundByOrder(dto.getOrderId(), dto.getPayMoney().toString());
                if (result1.success()) {
                    boolean b = (boolean) result1.getData();
                    if (!b) {
                        return new Result().error("退款失败！");
                    }else {

                    }
                } else {
                    return new Result().error(result1.getMsg());
                }
            } else {
                return new Result().error("无法退款！");
            }
        } else {
            return new Result().error("无法获取订单！");
        }
        if(StringUtils.isNoneBlank(clientId)){
            AppPushUtil.pushToSingleClient("商家同意退单", "您的退单申请已通过审核！", "", clientId);
        }
        return new Result();
    }

    /**
     * 拒绝退款订单
     *
     * @param id
     * @param status
     * @param verify
     * @param verify_date
     * @param refundReason
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundNoUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        ClientUserDTO clientUserDTO = clientUserService.get(dto.getCreator());
        String clientId = clientUserDTO.getClientId();
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
                baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
                }
                if(StringUtils.isNoneBlank(clientId)){
                    AppPushUtil.pushToSingleClient("商家拒绝接单", "您的订单商家已拒绝", "", clientId);
                }
            } else {
                return new Result().error("无法退款！");
            }
        } else {
            return new Result().error("无法获取订单！");
        }
        return new Result().ok("拒绝退款成功！");
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Object updateStatusAndReason(long id, int status, long verify, Date verify_date, String verify_reason) throws Exception {
//        MasterOrderDTO masterOrderDTO = masterOrderService.get(id);
//        OrderDTO order = masterOrderService.getOrder(masterOrderDTO.getOrderId());
//        Map<String, String> reqData = new HashMap<>();
//        Map<String, String> resultMap=new HashMap<>();
//        if(order.getStatus()==6){
//            // 商户订单号
//            reqData.put("out_trade_no", order.getOrderId());
//            //获取用户ID
//            ClientUserEntity userByPhone = clientUserService.getUserByPhone(order.getContacts());
//            // 授权码
//            reqData.put("out_refund_no", OrderUtil.getRefundOrderIdByTime(userByPhone.getId()));
//
//            // 订单总金额，单位为分，只能为整数
//            BigDecimal payMoney = order.getPayMoney();
//            BigDecimal total = payMoney.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
//            java.text.DecimalFormat df=new java.text.DecimalFormat("0");
//            reqData.put("total_fee", df.format(total));
//            //退款金额
//            BigDecimal refund = payMoney.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
//            reqData.put("refund_fee", df.format(refund));
//            // 退款异步通知地址
//            reqData.put("notify_url", wxPayConfig.getNotifyUrl());
//            reqData.put("refund_fee_type", "CNY");
//            reqData.put("op_user_id", wxPayConfig.getMchID());
//            resultMap = wxPay.refund(reqData);
//        }
//        int i = baseDao.updateStatusAndReason(id, status, verify, verify_date, verify_reason);
//        List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(masterOrderDTO.getOrderId());
//        for (SlaveOrderEntity s:slaveOrderEntities) {
//            slaveOrderService.updateSlaveOrderStatus(Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue(),s.getOrderId(),s.getGoodId());
//        }
//        Result result=new Result();
//        if(result.getCode()==200){
//            MasterOrderDTO dto= masterOrderService.get(id);
//            if(null!=dto){
//                ClientUserDTO userDto= clientUserService.get(dto.getCreator());
//                if(null!=userDto){
//                    String clientId=userDto.getClientId();
//                    if(StringUtils.isNotBlank(clientId)){
//                        //发送个推消息
//                        AppPushUtil.pushToSingleClient("订单管理","接受订单","",
//                                clientId);
//                    }else{
//                        result.error("没有获取到clientid!");
//                        return result;
//                    }
//                }
//            }
//        }
//        return resultMap;
//
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO getOrder(String orderId) {
        BigDecimal a = new BigDecimal(0);
        BigDecimal b = new BigDecimal(0);
        MasterOrderEntity masterOrderEntity = baseDao.selectByOrderId(orderId);
        OrderDTO orderDTO = ConvertUtils.sourceToTarget(masterOrderEntity, OrderDTO.class);

        //商家信息
        MerchantEntity merchantEntity = merchantService.selectById(masterOrderEntity.getMerchantId());
        orderDTO.setMerchantInfo(merchantEntity);
        //加菜信息
        List<MasterOrderEntity> masterOrderEntities = baseDao.selectPOrderId(orderId);
        //查找加菜订单未支付订单
        List<MasterOrderEntity> masterOrderEntities2 = baseDao.selectPOrderIdAndS1(orderId);
        masterOrderEntities.removeAll(masterOrderEntities2);
        List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(orderId);
        List list = new ArrayList();
        for (MasterOrderEntity orderEntity : masterOrderEntities) {
            List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(orderEntity.getOrderId());
            for (int j = 0; j < slaveOrderEntities.size(); j++) {
                SlaveOrderEntity slaveOrderEntity = slaveOrderEntities.get(j);
                GoodEntity goodEntity = goodService.selectById(slaveOrderEntity.getGoodId());
                slaveOrderEntity.setGoodInfo(goodEntity);
            }
            list.add(slaveOrderEntities);
            orderDTO.setSlaveOrder(list);


        }
        for (MasterOrderEntity orderEntity : masterOrderEntities1) {
            a = a.add(orderEntity.getPayMoney());
        }
        b = a.add(orderDTO.getPayMoney());
        orderDTO.setPpaymoney(a);
        orderDTO.setAllPaymoney(b);
//        //菜单信息
//        List<SlaveOrderEntity> slaveOrderEntitys = slaveOrderService.selectByOrderId(orderId);
//        int size=slaveOrderEntitys.size();
//        for (int i = 0; i < size; i++) {
//            SlaveOrderEntity slaveOrderEntity=slaveOrderEntitys.get(i);
//            GoodEntity goodEntity = goodService.selectById(slaveOrderEntity.getGoodId());
//            slaveOrderEntity.setGoodInfo(goodEntity);
//        }
        //包房ID不为空并且订单状态为3（只预定菜）的时候
        //查询出是否存在与主单相关联的包房订单
        if (masterOrderEntity.getRoomId() == null && masterOrderEntity.getReservationType() == Constants.ReservationType.ONLYGOODRESERVATION.getValue()) {
            MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
            //判断存在关联包房订单，并且包房状态为未支付
            if (roomOrderByPorderId != null && roomOrderByPorderId.getStatus() == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
                MerchantRoomEntity merchantRoomEntity = merchantRoomService.selectById(roomOrderByPorderId.getRoomId());
                orderDTO.setMerchantRoomEntity(merchantRoomEntity);
                MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(masterOrderEntity.getReservationId());
                orderDTO.setReservationInfo(merchantRoomParamsSetEntity);
            }
        }
        //包房ID不为空并且订单状态为1（正常预定）的时候
        if (masterOrderEntity.getRoomId() != null && masterOrderEntity.getReservationType() == Constants.ReservationType.NORMALRESERVATION.getValue()) {
            MerchantRoomEntity merchantRoomEntity = merchantRoomService.selectById(masterOrderEntity.getRoomId());
            orderDTO.setMerchantRoomEntity(merchantRoomEntity);
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(masterOrderEntity.getReservationId());
            orderDTO.setReservationInfo(merchantRoomParamsSetEntity);
        }

        return orderDTO;
    }

    @Override
    public MasterOrderEntity selectByOrderId(String orderId) {
        return baseDao.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result orderSave(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user) {
        Result result = new Result();
        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        Integer reservationType = dto.getReservationType();

        if (reservationType != Constants.ReservationType.ONLYGOODRESERVATION.getValue()) {
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(dto.getReservationId());
            if (merchantRoomParamsSetEntity == null) {
                return result.error(-5, "没有此包房/散台");
            }
            int isUse = merchantRoomParamsSetEntity.getState();
            if (isUse == 0) {
             merchantRoomParamsSetEntity.setState(1);

            } else if (isUse == 1) {
                return result.error(-1, "包房/散台已经预定,请重新选择！");
            }
        }


        //是否使用赠送金
        if (dto.getGiftMoney() != null && dto.getGiftMoney().doubleValue() > 0) {
            ClientUserEntity clientUserEntity = clientUserService.selectById(user.getId());
            BigDecimal gift = clientUserEntity.getGift();
            BigDecimal useGift = new BigDecimal(dto.getGiftMoney().toString());
            useGift = useGift.setScale(2);
            if (gift.compareTo(useGift) == -1) {
                return result.error(-7, "您的赠送金不足！");
            } else {
                clientUserEntity.setGift(gift.subtract(useGift));
            }
            clientUserService.updateById(clientUserEntity);
        }
        Date d = new Date();
        //保存主订单
        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setStatus(Constants.OrderStatus.NOPAYORDER.getValue());
        //如果包房押金未0，先房后菜情况下设置订单状态未已支付
        if (dto.getReservationType()==2&&dto.getPayMoney().compareTo(BigDecimal.ZERO)==0){
            masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());

        }

        masterOrderEntity.setInvoice("0");
        masterOrderEntity.setCreator(user.getId());
        masterOrderEntity.setCreateDate(d);
        MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
        if(reservationType == Constants.ReservationType.ONLYROOMRESERVATION.getValue()&&merchantDTO.getDepost()==0){
                if(dtoList == null){
                    masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                }
        }
        BigDecimal a=new BigDecimal("0");
        BigDecimal b=new BigDecimal("0");
        if(dtoList!=null){
        for (SlaveOrderEntity s:dtoList) {
            a=a.add(s.getPlatformBrokerage());
            b=b.add(s.getMerchantProceeds());
        }
        }
        masterOrderEntity.setPlatformBrokerage(a);
        masterOrderEntity.setMerchantProceeds(b);
        int i = baseDao.insert(masterOrderEntity);
        if (i <= 0) {
            return result.error(-2, "没有订单数据！");
        }
        //保存订单菜品
        if (masterOrderEntity.getReservationType() != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            if (dtoList == null) {
                return result.error(-6, "没有菜品数据！");
            }
            int size = dtoList.size();
            for (int n = 0; n < size; n++) {
                SlaveOrderEntity slaveOrderEntity = dtoList.get(n);
                slaveOrderEntity.setOrderId(orderId);
                slaveOrderEntity.setStatus(1);
                slaveOrderService.insert(slaveOrderEntity);
            }
//        List<SlaveOrderEntity> slaveOrderEntityList=ConvertUtils.sourceToTarget(dtoList,SlaveOrderEntity.class);
//            boolean b=slaveOrderService.insertBatch(dtoList);

        }
        return result.ok(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveOrder(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user) {
        Result result = new Result();
        //加菜订单
        //只有正常预订和只点菜才可以加菜
        if (Constants.ReservationType.ONLYROOMRESERVATION.getValue() == dto.getReservationType()) {
            return result.error(-11, "只预订包房不可以加菜！");
        }
        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        //是否使用赠送金
        if (dto.getGiftMoney() != null && dto.getGiftMoney().doubleValue() > 0) {
            ClientUserEntity clientUserEntity = clientUserService.selectById(user.getId());
            BigDecimal gift = clientUserEntity.getGift();
            BigDecimal useGift = new BigDecimal(dto.getGiftMoney().toString());
            useGift = useGift.setScale(2);
            if (gift.compareTo(useGift) == -1) {
                return result.error(-7, "您的赠送金不足！");
            } else {
                clientUserEntity.setGift(gift.subtract(useGift));
            }
            clientUserService.updateById(clientUserEntity);
        }
        Date d = new Date();
        //保存主订单
        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setPOrderId(dto.getOrderId());
        baseDao.updateById(masterOrderEntity);
        masterOrderEntity.setStatus(Constants.OrderStatus.NOPAYORDER.getValue());
        masterOrderEntity.setReservationType(dto.getReservationType());
        masterOrderEntity.setInvoice("0");
        masterOrderEntity.setCreator(user.getId());
        masterOrderEntity.setCreateDate(d);
        int i = baseDao.insert(masterOrderEntity);
        if (i <= 0) {
            return result.error(-2, "没有订单数据！");
        }
        //保存订单菜品
        if (masterOrderEntity.getReservationType() != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            if (dtoList == null) {
                return result.error(-6, "没有菜品数据！");
            }
            int size = dtoList.size();
            for (int n = 0; n < size; n++) {
                SlaveOrderEntity slaveOrderEntity = dtoList.get(n);
                slaveOrderEntity.setOrderId(orderId);
                slaveOrderEntity.setStatus(1);
                slaveOrderService.insert(slaveOrderEntity);
            }
//        List<SlaveOrderEntity> slaveOrderEntityList=ConvertUtils.sourceToTarget(dtoList,SlaveOrderEntity.class);
//            boolean b=slaveOrderService.insertBatch(dtoList);

        }
        return result.ok(orderId);
    }

    @Override
    public PageData<OrderDTO> listPage(Map<String, Object> params) {
        IPage<MasterOrderEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                getQueryWrapper(params)
        );

        List<MasterOrderEntity> masterOrderEntities = page.getRecords();
        int size = masterOrderEntities.size();
        for (int n = 0; n < size; n++) {
            MasterOrderEntity masterOrderEntity = masterOrderEntities.get(n);
            BigDecimal a = masterOrderEntity.getPayMoney();
            List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(masterOrderEntity.getOrderId());
            for (MasterOrderEntity orderEntity : masterOrderEntities1) {
                a = a.add(orderEntity.getPayMoney());
            }
            masterOrderEntity.setPayMoney(a);
            //商家信息
            MerchantEntity merchantEntity = merchantService.selectById(masterOrderEntity.getMerchantId());
            masterOrderEntity.setMerchantInfo(merchantEntity);
            //菜单信息
            List<SlaveOrderEntity> slaveOrderEntitys = slaveOrderService.selectByOrderId(masterOrderEntity.getOrderId());
            int size1 = slaveOrderEntitys.size();
            for (int i = 0; i < size1; i++) {
                SlaveOrderEntity slaveOrderEntity = slaveOrderEntitys.get(i);
                GoodEntity goodEntity = goodService.selectById(slaveOrderEntity.getGoodId());
                slaveOrderEntity.setGoodInfo(goodEntity);
            }
            masterOrderEntity.setSlaveOrder(slaveOrderEntitys);
        }
        return getPageData(page, OrderDTO.class);
    }

    /**
     * 商户端订单列表查询
     *
     * @param params
     * @return
     */
    @Override
    public PageData<MerchantOrderDTO> listMerchantPage(Map<String, Object> params) {
        //int count= baseDao.selectCount(getWrapper(params));
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        String status = params.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            String[] str = status.split(",");
            params.put("statusStr", str);
        }
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        } else {
            params.put("merchantId", null);
        }
        List<MerchantOrderDTO> list = baseDao.listMerchant(params);
//        for (MerchantOrderDTO orderDTO : list) {
//
//                BigDecimal a = orderDTO.getPayMoney();
//
//            List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(orderDTO.getOrderId());
//            for (MasterOrderEntity orderEntity : masterOrderEntities1) {
//                a = a.add(orderEntity.getPayMoney());
//            }
//            orderDTO.setPayMoney(a);
//        }
        return getPageData(list, pages.getTotal(), MerchantOrderDTO.class);
    }

    /**
     * 商户端订单预约列表查询
     *
     * @param params
     * @return
     */
    @Override
    public PageData<MerchantOrderDTO> listMerchantPage2(Map<String, Object> params) {
        //int count= baseDao.selectCount(getWrapper(params));
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        String status = params.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            String[] str = status.split(",");
            params.put("statusStr", str);
        }
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        } else {
            params.put("merchantId", null);
        }
        List<MerchantOrderDTO> list = baseDao.listMerchant2(params);
//        for (MerchantOrderDTO orderDTO : list) {
//            BigDecimal a = orderDTO.getPayMoney();
//            List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(orderDTO.getOrderId());
//            for (MasterOrderEntity orderEntity : masterOrderEntities1) {
//                a = a.add(orderEntity.getPayMoney());
//            }
//            orderDTO.setPayMoney(a);
//        }
        return getPageData(list, pages.getTotal(), MerchantOrderDTO.class);
    }

    @Override
    public Result updateByCheck(Long id) {
        Result result = new Result();
        MasterOrderEntity masterOrderEntity = baseDao.selectById(id);
        int s = masterOrderEntity.getStatus();
        if (s != 4) {
            return result.error(-1, "不是未支付订单,不能取消订单！");
        }
        masterOrderEntity.setCheckStatus(1);
        masterOrderEntity.setCheckMode(Constants.CheckMode.USERCHECK.getValue());
        int i = baseDao.updateById(masterOrderEntity);
        if (i > 0) {
            result.ok(true);
        } else {
            return result.error(-1, "结账失败！请联系商家！");
        }
        return result;
    }

    @Override
    public List<MasterOrderEntity> selectPOrderId(String orderId) {
        return baseDao.selectPOrderId(orderId);
    }

    @Override
    public Result updateByCancel(Map<String, Object> params) {
        Result result = new Result();
        MasterOrderEntity masterOrderEntity = baseDao.selectById(Convert.toLong(params.get("id")));
        int s = masterOrderEntity.getStatus();
        if (s != 1) {
            return result.error(-1, "不是未支付订单,不能取消订单！");
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.CANCELNOPAYORDER.getValue());
        String refundReason = (String) params.get("refundReason");
        if (StringUtils.isNotBlank(refundReason)) {
            masterOrderEntity.setRefundReason(refundReason);
        }
        int i = baseDao.updateById(masterOrderEntity);
        if (i > 0) {
            result.ok(true);
        } else {
            return result.error(-1, "取消订单失败！");
        }
        return result;

    }

    @Override
    public Result updateByApplyRefund(Map<String, Object> params) {
        Result result = new Result();
        MasterOrderEntity masterOrderEntity = baseDao.selectById(Convert.toLong(params.get("id")));
        MerchantDTO merchantDTO = merchantService.get(masterOrderEntity.getMerchantId());
        MerchantUserDTO merchantUserDTO = merchantUserService.get(merchantDTO.getCreator());
        String clientId = merchantUserDTO.getClientId();
        int s = masterOrderEntity.getStatus();
        if (s == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
            masterOrderEntity.setStatus(Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue());
            String refundReason = (String) params.get("refundReason");
            if (StringUtils.isNotBlank(refundReason)) {
                masterOrderEntity.setRefundReason(refundReason);
            }
            int i = baseDao.updateById(masterOrderEntity);
            if (i > 0) {
                if (StringUtils.isNotBlank(clientId)) {
                    AppPushUtil.pushToSingleMerchant("订单管理", "您有退款信息，请及时处理退款！", "", clientId);
                }
                result.ok(true);
            } else {
                return result.error(-1, "申请退款失败！");
            }
        } else {
            return result.error(-1, "订单不能申请退款！");
        }
        return result;
    }

    @Override
    public Map<String, String> getNotify(Constants.PayMode alipay, BigDecimal bigDecimal, String out_trade_no) {

        return null;
    }


    private Wrapper<MasterOrderEntity> getQueryWrapper(Map<String, Object> params) {
        String userId = (String) params.get("userId");
        //状态
        String pOrderId = (String) params.get("pOrderId");
        String status = (String) params.get("status");
        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(userId), "creator", userId);
        if (StringUtils.isNotBlank(status) && status.indexOf(",") > -1) {
            wrapper.in(StringUtils.isNotBlank(status), "status", status);
        } else {
            wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        }
        wrapper.eq(StringUtils.isNotBlank(pOrderId), "p_order_id", pOrderId);
        return wrapper;
    }


    /**
     * 计算优惠卷
     *
     * @param dct
     * @return
     */

    public DesignConditionsDTO calculateCoupon(DesignConditionsDTO dct) {
        //获取此订餐所有菜品信息
        List<calculationAmountDTO> slaveOrder = dct.getSlaveOrder();
        //优惠券辅助运算
        BigDecimal discount = new BigDecimal("0");
        BigDecimal disc = new BigDecimal(0.1).setScale(2, BigDecimal.ROUND_DOWN);
        //订单原总价
        BigDecimal totalMoney1 = dct.getTotalMoney();

        MerchantCouponDTO merchantCouponDTO = merchantCouponService.get(dct.getId());
        if (merchantCouponDTO.getDisType() != 2) {
            Double money = merchantCouponDTO.getDiscount();
            //获取优惠卷金额
            discount = new BigDecimal(money).setScale(2, BigDecimal.ROUND_DOWN);
        } else if (merchantCouponDTO.getDisType() == 2) {
            Double money = merchantCouponDTO.getDiscount();
            BigDecimal discounts = new BigDecimal(money);
            //折扣后价格
            BigDecimal bigDecimaMoney = totalMoney1.multiply(discounts.multiply(disc)).setScale(2, BigDecimal.ROUND_DOWN);
            discount = totalMoney1.subtract(bigDecimaMoney);
        }

        //优惠卷辅助运算
        BigDecimal x = new BigDecimal(0);
//---------------------------------------------------平台扣点算法-----------------------------------------------------
        //扣点基数
        BigDecimal base=new BigDecimal("1");
        //扣点比例
        BigDecimal ratio=new BigDecimal("0.15");
        BigDecimal payMoney = totalMoney1.subtract(discount);
        //商户所得金额
        BigDecimal subtract2 = payMoney.multiply(base.subtract(ratio));
        //平台扣点金额
        BigDecimal subtract3 = payMoney.multiply(ratio);


        /*优惠卷算法*/


        ArrayList<calculationAmountDTO> slaveOrderEntityArrayList = new ArrayList<>();
        for (int i = 0; i < slaveOrder.size(); i++) {
            //一条对象
            calculationAmountDTO slaveOrderEnti = slaveOrder.get(i);
            //获取菜品单价
            BigDecimal price = slaveOrder.get(i).getPrice();
            //单个菜品数量
            BigDecimal quantity = slaveOrder.get(i).getQuantity();
            if (slaveOrder.size() - i == 1 || slaveOrder.size() == 1) {
                BigDecimal newPrive = price.subtract((discount.subtract(x).divide(quantity))).multiply(quantity);
                slaveOrderEnti.setNewPrice(newPrive);
                //维护菜品详细表中赠送金字段，（原价-优惠后单价）*数量=此菜品共优惠多少钱
                slaveOrderEnti.setDiscountsMoney(discount.subtract(x));
                slaveOrderEnti.setTotalMoney(newPrive.multiply(quantity));
                slaveOrderEnti.setMerchantProceeds(subtract2);
                slaveOrderEnti.setPlatformBrokerage(subtract3);
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
            } else {
                //原单价/原总价
                BigDecimal divide = price.divide(totalMoney1, 2, BigDecimal.ROUND_DOWN);
                //（原单价/原总价）*优惠卷总额=一个菜品优惠金额
                BigDecimal multiply = divide.multiply(discount).setScale(2, BigDecimal.ROUND_DOWN);
                //优惠卷平均到单个菜品金额=菜品单价-（菜品单价/订单总金额）*赠送金总金额
                BigDecimal aGoodfreeGoldMoney = price.subtract((multiply)).setScale(2, BigDecimal.ROUND_DOWN);
                slaveOrderEnti.setNewPrice(aGoodfreeGoldMoney);
                BigDecimal subtract = multiply.multiply(quantity);
                slaveOrderEnti.setDiscountsMoney(subtract);
                //实际支付总金额（扣除掉优惠券平均到菜品的钱）
                BigDecimal multiply1 = aGoodfreeGoldMoney.multiply(quantity);
                slaveOrderEnti.setTotalMoney(multiply1);
                BigDecimal subtract1 = base.subtract(ratio);
                //计算平台扣点金额
                slaveOrderEnti.setPlatformBrokerage(multiply1.multiply(ratio));
                subtract3=subtract3.subtract(multiply1.multiply(ratio)).setScale(2, BigDecimal.ROUND_DOWN);
                slaveOrderEnti.setMerchantProceeds(multiply1.multiply(subtract1));
                //计算商户实际可得金额
                subtract2=subtract2.subtract(multiply1.multiply(multiply1.multiply(subtract1)).setScale(2, BigDecimal.ROUND_DOWN));
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
                x = x.add(price.subtract(price.subtract(subtract)));

            }
            slaveOrderEntityArrayList.add(slaveOrderEnti);

        }
        dct.setSlaveOrder(slaveOrderEntityArrayList);

        return dct;
    }

    /**
     * 计算赠送金
     *
     * @param dct
     * @return
     */

    public DesignConditionsDTO calculateGift(DesignConditionsDTO dct) {
        //获取此订餐所有菜品信息
        List<calculationAmountDTO> slaveOrder = dct.getSlaveOrder();
        //获取减免金额类型("扣减金额类型：0默认未扣减、1赠送金扣减、2优惠卷扣减、3优惠卷与赠送金同时使用")
        Integer preferentialType = dct.getPreferentialType();
        Double money = 0.0;

        //获取优惠卷金额
        BigDecimal discount = new BigDecimal(money);
        //赠送金比例
        BigDecimal freeGoldProportion = new BigDecimal(0.1);
        BigDecimal a = new BigDecimal(0);
        BigDecimal x = new BigDecimal(0);
        /*赠送金算法*/

        //订单原总价
        BigDecimal totalMoney1 = dct.getTotalMoney();

        //---------------------------------------------------平台扣点算法-----------------------------------------------------
        //扣点基数
        BigDecimal base=new BigDecimal("1");
        //扣点比例
        BigDecimal ratio=new BigDecimal("0.15");
        BigDecimal payMoney = totalMoney1.subtract(discount);
        //商户所得金额
        BigDecimal subtract2 = payMoney.multiply(base.subtract(ratio));
        //平台扣点金额
        BigDecimal subtract3 = payMoney.multiply(ratio);


        ArrayList<calculationAmountDTO> slaveOrderEntityArrayList = new ArrayList<>();
        for (int i = 0; i < slaveOrder.size(); i++) {
            //一条对象
            calculationAmountDTO slaveOrderEnti = slaveOrder.get(i);
            //获取菜品单价
            BigDecimal price = slaveOrder.get(i).getPrice();
            //单个菜品数量
            BigDecimal quantity = slaveOrder.get(i).getQuantity();
            //赠送金总金额
            BigDecimal freeGoldMoney = totalMoney1.multiply(freeGoldProportion).setScale(2, BigDecimal.ROUND_DOWN);
            //获取用户信息
            ClientUserDTO clientUserDTO = clientUserService.get(dct.getUserId());
            BigDecimal gift = clientUserDTO.getGift();
            if (gift.compareTo(freeGoldMoney) == -1) {
                freeGoldMoney = gift;
            }
            if (slaveOrder.size() - i == 1 || slaveOrder.size() == 1) {
                BigDecimal newPrice = price.subtract((freeGoldMoney.subtract(x).divide(quantity)));
                slaveOrderEnti.setNewPrice(newPrice);
                slaveOrderEnti.setTotalMoney(newPrice.multiply(quantity));
                slaveOrderEnti.setPlatformBrokerage(subtract3);
                slaveOrderEnti.setMerchantProceeds(subtract2);
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
                //维护菜品详细表中赠送金字段，（原价-优惠后单价）*数量=此菜品共优惠多少钱
                slaveOrderEnti.setFreeGold(freeGoldMoney.subtract(x));
            } else {
                //原单价/原总价
                BigDecimal divide = price.divide(totalMoney1, 2, BigDecimal.ROUND_DOWN);
                //（原单价/原总价）*赠送金总额=一个菜品优惠金额
                BigDecimal multiply = divide.multiply(freeGoldMoney).setScale(2, BigDecimal.ROUND_DOWN);
                //赠送金平均到单个菜品金额=菜品单价-（菜品单价/订单总金额）*赠送金总金额
                BigDecimal aGoodfreeGoldMoney = price.subtract((multiply)).setScale(2, BigDecimal.ROUND_DOWN);
                BigDecimal multiply1 = price.multiply(quantity);
                //平台所得金额
                BigDecimal platformBrokerage = multiply1.multiply(ratio);
                slaveOrderEnti.setPlatformBrokerage(platformBrokerage);
                subtract3=subtract3.subtract(platformBrokerage);
                //商户所的金额
                BigDecimal merchantProceeds = multiply1.multiply(base.subtract(ratio));
                subtract2=subtract2.subtract(merchantProceeds);

                slaveOrderEnti.setMerchantProceeds(merchantProceeds);
                slaveOrderEnti.setPlatformBrokerage(platformBrokerage);
                slaveOrderEnti.setNewPrice(aGoodfreeGoldMoney);
                BigDecimal subtract = multiply.multiply(quantity);
                slaveOrderEnti.setFreeGold(subtract);
                slaveOrderEnti.setTotalMoney(aGoodfreeGoldMoney.multiply(quantity));
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
                x = x.add(price.subtract(price.subtract(subtract)));

            }
            slaveOrderEntityArrayList.add(slaveOrderEnti);

        }
        dct.setSlaveOrder(slaveOrderEntityArrayList);

        return dct;
    }

    /**
     * 计算赠送金与优惠卷
     *
     * @param dct
     * @return
     */

    public DesignConditionsDTO calculateGiftCoupon(DesignConditionsDTO dct) {
        DesignConditionsDTO designConditionsDTO = this.calculateCoupon(dct);
        List<calculationAmountDTO> slaveOrder = designConditionsDTO.getSlaveOrder();
        BigDecimal totalMoney = dct.getTotalMoney();
        BigDecimal s = new BigDecimal(0);
        BigDecimal c = new BigDecimal(0);

        for (int i = 0; i < slaveOrder.size(); i++) {
            BigDecimal price = slaveOrder.get(i).getNewPrice();
            BigDecimal quantity = slaveOrder.get(i).getQuantity();
            BigDecimal multiply = price.multiply(quantity);
            s = s.add(multiply);
            c = c.add(price);
            slaveOrder.get(i).setNewPrice(price);
            slaveOrder.get(i).setTotalMoney(multiply);
        }
        dct.setTotalMoney(totalMoney);
        //获取减免金额类型("扣减金额类型：0默认未扣减、1赠送金扣减、2优惠卷扣减、3优惠卷与赠送金同时使用")
        Integer preferentialType = dct.getPreferentialType();
        Double money = 0.0;

        //获取优惠卷金额
        BigDecimal discount = new BigDecimal(money);
        //赠送金比例
        BigDecimal freeGoldProportion = new BigDecimal(0.1);
        BigDecimal a = new BigDecimal(0);
        BigDecimal x = new BigDecimal(0);
        /*赠送金算法*/


        ArrayList<calculationAmountDTO> slaveOrderEntityArrayList = new ArrayList<>();
        for (int i = 0; i < slaveOrder.size(); i++) {
            //一条对象
            calculationAmountDTO slaveOrderEnti = slaveOrder.get(i);
            //获取菜品单价
            BigDecimal price = slaveOrder.get(i).getNewPrice();
            //单个菜品数量
            BigDecimal quantity = slaveOrder.get(i).getQuantity();
            //赠送金总金额
            BigDecimal freeGoldMoney = s.multiply(freeGoldProportion).setScale(2, BigDecimal.ROUND_DOWN);
            //获取用户信息
            ClientUserDTO clientUserDTO = clientUserService.get(dct.getUserId());
            BigDecimal gift = clientUserDTO.getGift();
            if (gift.compareTo(freeGoldMoney) == -1) {
                freeGoldMoney = gift;
            }
            if (slaveOrder.size() - i == 1 || slaveOrder.size() == 1) {
                BigDecimal newPrice = price.subtract((freeGoldMoney.subtract(x).divide(quantity)));
                slaveOrderEnti.setNewPrice(newPrice);
                slaveOrderEnti.setTotalMoney(newPrice.multiply(quantity));
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setMerchantProceeds(slaveOrder.get(i).getMerchantProceeds());
                slaveOrderEnti.setPlatformBrokerage(slaveOrder.get(i).getPlatformBrokerage());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
                //维护菜品详细表中赠送金字段，（原价-优惠后单价）*数量=此菜品共优惠多少钱
                slaveOrderEnti.setFreeGold(freeGoldMoney.subtract(x));
            } else {
                //原单价/原总价
                BigDecimal divide = price.divide(s, 2, BigDecimal.ROUND_DOWN);
                //（原单价/原总价）*赠送金总额=一个菜品优惠金额
                BigDecimal multiply = divide.multiply(freeGoldMoney).setScale(2, BigDecimal.ROUND_DOWN);
                //赠送金平均到单个菜品金额=菜品单价-（菜品单价/订单总金额）*赠送金总金额
                BigDecimal aGoodfreeGoldMoney = price.subtract((multiply)).setScale(2, BigDecimal.ROUND_DOWN);
                slaveOrderEnti.setNewPrice(aGoodfreeGoldMoney);
                BigDecimal subtract = multiply.multiply(quantity);
                slaveOrderEnti.setFreeGold(subtract);
                slaveOrderEnti.setMerchantProceeds(slaveOrder.get(i).getMerchantProceeds());
                slaveOrderEnti.setPlatformBrokerage(slaveOrder.get(i).getPlatformBrokerage());
                slaveOrderEnti.setTotalMoney(aGoodfreeGoldMoney.multiply(quantity));
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
                x = x.add(price.subtract(price.subtract(subtract)));

            }
            slaveOrderEntityArrayList.add(slaveOrderEnti);

        }
        dct.setSlaveOrder(slaveOrderEntityArrayList);

        return dct;
    }

    /**
     * 无任何优惠（只计算扣点）
     *
     * @param dct
     * @return
     */
    @Override
    public DesignConditionsDTO notDiscounts(DesignConditionsDTO dct) {
        //获取此订餐所有菜品信息
        List<calculationAmountDTO> slaveOrder = dct.getSlaveOrder();

        //订单原总价
        BigDecimal totalMoney1 = dct.getTotalMoney();

        //---------------------------------------------------平台扣点算法-----------------------------------------------------
        //扣点基数
        BigDecimal base=new BigDecimal("1");
        //扣点比例
        BigDecimal ratio=new BigDecimal("0.15");
        //商户所得金额
        BigDecimal subtract2 = totalMoney1.multiply(base.subtract(ratio));
        //平台扣点金额
        BigDecimal subtract3 = totalMoney1.multiply(ratio);


        ArrayList<calculationAmountDTO> slaveOrderEntityArrayList = new ArrayList<>();
        for (int i = 0; i < slaveOrder.size(); i++) {
            //一条对象
            calculationAmountDTO slaveOrderEnti = slaveOrder.get(i);
            //获取菜品单价
            BigDecimal price = slaveOrder.get(i).getPrice();
            //单个菜品数量
            BigDecimal quantity = slaveOrder.get(i).getQuantity();
            //此菜品总价
            BigDecimal AGreensMoney = price.multiply(quantity);
            //获取用户信息
            ClientUserDTO clientUserDTO = clientUserService.get(dct.getUserId());

            if (slaveOrder.size() - i == 1 || slaveOrder.size() == 1) {
                slaveOrderEnti.setTotalMoney(AGreensMoney);
                slaveOrderEnti.setPlatformBrokerage(subtract3);
                slaveOrderEnti.setMerchantProceeds(subtract2);
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
            } else {
                //平台所得金额
                BigDecimal platformBrokerage = AGreensMoney.multiply(ratio);
                slaveOrderEnti.setPlatformBrokerage(platformBrokerage);
                subtract3=subtract3.subtract(platformBrokerage);
                //商户所的金额
                BigDecimal merchantProceeds = AGreensMoney.multiply(base.subtract(ratio));
                subtract2=subtract2.subtract(merchantProceeds);

                slaveOrderEnti.setMerchantProceeds(merchantProceeds);
                slaveOrderEnti.setPlatformBrokerage(platformBrokerage);
                slaveOrderEnti.setNewPrice(price);
                slaveOrderEnti.setTotalMoney(AGreensMoney);
                GoodDTO goodDTO = goodService.get(slaveOrder.get(i).getGoodId());
                slaveOrderEnti.setName(goodDTO.getName());
                slaveOrderEnti.setIcon(goodDTO.getIcon());
            }
            slaveOrderEntityArrayList.add(slaveOrderEnti);

        }
        dct.setSlaveOrder(slaveOrderEntityArrayList);
        return dct;
    }

    @Override
    public void updateOrderStatus(int status, String orderId) {
        baseDao.updateOrderStatus(status, orderId);
    }

    @Override
    public void updatePayMode(String payMode, String orderId) {
        baseDao.updatePayMode(payMode, orderId);
    }

    @Override
    public MasterOrderDTO getOrderById(long id) {
        return baseDao.getOrderById(id);
    }

    @Override
    public Result caleclUpdate(long id, long verify, Date date, String verify_reason) {
        MasterOrderDTO dto = get(id);
        int status = dto.getStatus();
        if (status == Constants.OrderStatus.NOPAYORDER.getValue()) {
            int status_new = Constants.OrderStatus.CANCELNOPAYORDER.getValue();
            baseDao.updateStatusAndReason(id, status_new, verify, date, verify_reason);
            List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
            for (SlaveOrderEntity s : slaveOrderEntities) {
                if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                    slaveOrderService.updateSlaveOrderStatus(status_new, s.getOrderId(), s.getGoodId());
                }
            }
            if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                //同时将包房或者桌设置成未使用状态
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
            }
            return new Result().ok("成功取消订单");
        } else if (status == Constants.OrderStatus.PAYORDER.getValue()) {
            int status_new = Constants.OrderStatus.MERCHANTREFUSALORDER.getValue();
            baseDao.updateStatusAndReason(id, status_new, verify, date, verify_reason);
            List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
            for (SlaveOrderEntity s : slaveOrderEntities) {
                if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                    slaveOrderService.updateSlaveOrderStatus(status_new, s.getOrderId(), s.getGoodId());
                }
            }
            if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                //同时将包房或者桌设置成未使用状态
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
            }
            //退款
            Result result1 = payService.refundByOrder(dto.getOrderId(), dto.getPayMoney().toString());
            if (result1.success()) {
                boolean b = (boolean) result1.getData();
                if (!b) {
                    return new Result().error("支付失败！");
                }
            } else {
                return new Result().error(result1.getMsg());
            }
            ClientUserDTO userDto = clientUserService.get(dto.getCreator());
            if (null != userDto) {
                String clientId = userDto.getClientId();
                if (StringUtils.isNotBlank(clientId)) {
                    //发送个推消息
                    AppPushUtil.pushToSingleClient("商家拒绝接单", "您的订单商家已拒绝", "", clientId);
                }
            }
        } else {
            return new Result().error("不能取消订单！");
        }
        return new Result();
    }

    @Override
    public Result cancelOrder(long id) {
        MasterOrderDTO dto = get(id);
        int status = dto.getStatus();
        if (status == Constants.OrderStatus.NOPAYORDER.getValue()) {
            int status_new = Constants.OrderStatus.CANCELNOPAYORDER.getValue();
            baseDao.updateStatusAndReason(id, status_new, dto.getCreator(), new Date(), null);
            BigDecimal a=new BigDecimal("0");
            List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
            for (SlaveOrderEntity s : slaveOrderEntities) {
                if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                    slaveOrderService.updateSlaveOrderStatus(status_new, s.getOrderId(), s.getGoodId());
                    slaveOrderService.updateSlaveOrderPointDeduction(a,a,s.getOrderId(),s.getGoodId());
                }
            }
            if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                //同时将包房或者桌设置成未使用状态
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());

            }
            masterOrderService.updateSlaveOrderPointDeduction(a,a,dto.getOrderId());

        } else {
            return new Result().error("无法取消订单！");
        }
        return new Result();
    }


    /**
     * 更新主订单实付金额
     *
     * @param PayMoney
     * @param orderId
     */
    @Override
    public void updatePayMoney(BigDecimal PayMoney, String orderId) {
        baseDao.updatePayMoney(PayMoney, orderId);
    }

    /**
     * 下单后预定包房
     *
     * @param dto:  主订单包房信息
     * @param user: 用户信息
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/10/11
     * @Return:
     */
    @Override
    public Result reserveRoom(OrderDTO dto, ClientUserEntity user, String mainOrderId) {
        Result result = new Result();
        List<MasterOrderEntity> masterOrderEntities = baseDao.selectPOrderIdByMainOrderID(mainOrderId);
           if (masterOrderEntities.size()!=0){
               return result.error("已预定包房，不可重复预定");
           }

        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        Integer reservationType = dto.getReservationType();
        MerchantRoomParamsSetEntity c = new MerchantRoomParamsSetEntity();
        if (reservationType == Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(dto.getReservationId());
            if (merchantRoomParamsSetEntity == null) {
                return result.error(-5, "没有此包房/散台");
            }
            int isUse = merchantRoomParamsSetEntity.getState();
            c = merchantRoomParamsSetEntity;
            if (isUse != 1) {
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), 1);

            } else {
                return result.error(-1, "包房/散台已经预定,请重新选择！");
            }
        }
        Date d = new Date();
        //保存主订单
        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setStatus(Constants.OrderStatus.NOPAYORDER.getValue());
        if (reservationType == Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            masterOrderEntity.setReservationType(Constants.ReservationType.ONLYROOMRESERVATION.getValue());
        }
        OrderDTO order = masterOrderService.getOrder(mainOrderId);
        if (order.getStatus() == Constants.OrderStatus.PAYORDER.getValue() || order.getStatus() == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
            merchantRoomParamsSetService.updateStatus(masterOrderEntity.getReservationId(), 1);
        }
        masterOrderEntity.setInvoice("0");
        masterOrderEntity.setCreator(user.getId());
        masterOrderEntity.setCreateDate(d);
        masterOrderEntity.setStatus(4);
        masterOrderEntity.setPOrderId(mainOrderId);
        masterOrderEntity.setRoomId(c.getRoomId());
        int i = baseDao.insert(masterOrderEntity);
        if (i <= 0) {
            return result.error(-2, "没有订单数据！");
        }
        return result.ok(orderId);
    }

    @Override
    public MasterOrderEntity getRoomOrderByPorderId(String orderId) {
        return baseDao.getRoomOrderByPorderId(orderId);
    }

    @Override
    public Result orderFoodByRoom(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user, String mainOrderId) {
        Result result = new Result();
        OrderDTO orderDTO = masterOrderService.getOrder(mainOrderId);
        Integer mainReservationType = orderDTO.getReservationType();
        if (mainReservationType != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            return result.error(-9, "非只预定包房，不可以订餐！");
        }
        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        Integer reservationType = dto.getReservationType();

        if (reservationType != Constants.ReservationType.ONLYGOODRESERVATION.getValue()) {
            return result.error(-5, "非只预订菜品,不可以订餐！");
        }
        //锁定包房/散台

        //是否使用赠送金
        if (dto.getGiftMoney() != null && dto.getGiftMoney().doubleValue() > 0) {
            ClientUserEntity clientUserEntity = clientUserService.selectById(user.getId());
            BigDecimal gift = clientUserEntity.getGift();
            BigDecimal useGift = new BigDecimal(dto.getGiftMoney().toString());
            useGift = useGift.setScale(2);
            if (gift.compareTo(useGift) == -1) {
                return result.error(-7, "您的赠送金不足！");
            } else {
                clientUserEntity.setGift(gift.subtract(useGift));
            }
            clientUserService.updateById(clientUserEntity);
        }
        Date d = new Date();
        //保存主订单
        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setStatus(Constants.OrderStatus.NOPAYORDER.getValue());
        masterOrderEntity.setInvoice("0");
        masterOrderEntity.setCreator(user.getId());
        masterOrderEntity.setCreateDate(d);
        masterOrderEntity.setPOrderId(mainOrderId);
        int i = baseDao.insert(masterOrderEntity);
        if (i <= 0) {
            return result.error(-2, "没有订单数据！");
        }
        //保存订单菜品
        if (masterOrderEntity.getReservationType() != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            if (dtoList == null) {
                return result.error(-6, "没有菜品数据！");
            }
            int size = dtoList.size();
            for (int n = 0; n < size; n++) {
                SlaveOrderEntity slaveOrderEntity = dtoList.get(n);
                slaveOrderEntity.setOrderId(orderId);
                slaveOrderEntity.setStatus(1);
                slaveOrderService.insert(slaveOrderEntity);
            }
//        List<SlaveOrderEntity> slaveOrderEntityList=ConvertUtils.sourceToTarget(dtoList,SlaveOrderEntity.class);
//            boolean b=slaveOrderService.insertBatch(dtoList);

        }
        return result.ok(orderId);
    }

    @Override
    public List<MasterOrderEntity> getOrderByPOrderId(String orderId) {
        return baseDao.getOrderByPOrderId(orderId);
    }

    @Override
    public PageData<OrderDTO> getAllMainOrder(Map<String, Object> params) {
        IPage<MasterOrderEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        List<OrderDTO> allMainOrder = baseDao.getAllMainOrder(params);

        for (OrderDTO s:allMainOrder) {
            BigDecimal allpayMoney=new BigDecimal("0");
            List<MasterOrderEntity> auxiliaryOrderByOrderId = baseDao.getAuxiliaryOrderByOrderId(s.getOrderId());
            if(auxiliaryOrderByOrderId!=null) {
                for (MasterOrderEntity ss : auxiliaryOrderByOrderId) {
                    allpayMoney = allpayMoney.add(ss.getPayMoney());
                }
            }
            Integer status = s.getStatus();
            if(s.getStatus()==Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue()||
                    s.getStatus()==Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()||
                    s.getStatus()==Constants.OrderStatus.PAYORDER.getValue()||
                    s.getStatus()==Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()){
                allpayMoney=allpayMoney.add(s.getPayMoney());
            }
            s.setAllpaymoneys(allpayMoney);
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
            for (SlaveOrderEntity order:orderGoods) {
                GoodEntity byid = goodService.getByid(order.getGoodId());
                order.setGoodInfo(byid);
            }
            s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
            s.setSlaveOrder(orderGoods);
            if(s.getRoomId()!=null){
                s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
            }
        }
        return getPageData(allMainOrder,pages.getTotal(), OrderDTO.class);
    }

    @Override
    public List<MasterOrderEntity> getAuxiliaryOrderByOrderId(String orderId) {
        return baseDao.getAuxiliaryOrderByOrderId(orderId);
    }

    @Override
    public List<OrderDTO> getAuxiliaryOrder(Map params) {
        return baseDao.getAuxiliaryOrder(params);
    }

    @Override
    public PageData<OrderDTO> pageGetAuxiliaryOrder(Map<String, Object> params) {
        IPage<MasterOrderEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        String orderId = params.get("orderId").toString();
        OrderDTO order = masterOrderService.getMasterOrder(orderId);

        List<OrderDTO> allMainOrder = baseDao.getAuxiliaryOrder(params);
        allMainOrder.add(order);
        for (OrderDTO s:allMainOrder) {
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
            for (SlaveOrderEntity og:orderGoods) {
                og.setGoodInfo(goodService.getByid(og.getGoodId()));
            }
            s.setSlaveOrder(orderGoods);
            s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
            if(s.getRoomId()!=null){
                s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
            }
        }
        long total = pages.getTotal();
        if(total!=0){
            total=total+1;
        }
        return getPageData(allMainOrder,total, OrderDTO.class);
    }


    @Override
    public OrderDTO getMasterOrder(String orderId) {
        return baseDao.getMasterOrder(orderId);
    }

    @Override
    public OrderDTO orderParticulars(String orderId) {
        OrderDTO order = baseDao.getOrder(orderId);
        List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(orderId);
        for (SlaveOrderEntity og:orderGoods) {
            og.setGoodInfo(goodService.getByid(og.getGoodId()));
        }
        order.setSlaveOrder(orderGoods);
        order.setClientUserInfo(clientUserService.getClientUser(order.getCreator()));
        if(order.getRoomId()!=null){
            order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(order.getRoomId()));
        }else if(order.getPOrderId().equals("0")) {
            MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
            if(roomOrderByPorderId!=null){
            order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(roomOrderByPorderId.getRoomId()));
            order.setRoomId(roomOrderByPorderId.getRoomId());
            order.setReservationId(roomOrderByPorderId.getReservationId());
            }
        }
        return order;

    }

    @Override
    public OrderDTO orderParticulars1(String orderId) {
        OrderDTO order = baseDao.getOrder(orderId);
        List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(orderId);
        for (SlaveOrderEntity og:orderGoods) {
            og.setGoodInfo(goodService.getByid(og.getGoodId()));
        }
        order.setSlaveOrder(orderGoods);
        order.setClientUserInfo(clientUserService.getClientUser(order.getCreator()));
        if(order.getRoomId()!=null){
            order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(order.getRoomId()));
        }else if(order.getPOrderId().equals("0")) {
            MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
            if(roomOrderByPorderId!=null){
                order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(roomOrderByPorderId.getRoomId()));
                order.setRoomId(roomOrderByPorderId.getRoomId());
                order.setReservationId(roomOrderByPorderId.getReservationId());
            }
        }
        StimmeDTO stimmeDTO = stimmeService.selectByOrderId(orderId);
        stimmeDTO.setStatus(1);//改为已查看
        stimmeService.update(stimmeDTO);
        return order;
    }

    @Override
    public List<MasterOrderEntity> selectPOrderIdHavePaid(String orderId) {
        return baseDao.selectPOrderIdHavePaid(orderId);
    }

    @Override
    public List<MasterOrderEntity> selectAgreeRefundOrder(String orderId) {
        return baseDao.selectAgreeRefundOrder(orderId);
    }

    /***
     *用户端支付完成列表
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/10/24
     * @param params:
     * @Return:
     */
    @Override
    public PageData<OrderDTO> selectPOrderIdHavePaids(Map<String, Object> params) {
        IPage<MasterOrderEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        params.put("status",Constants.OrderStatus.PAYORDER.getValue());
        List<OrderDTO> allMainOrder = baseDao.getPayOrder(params);

        for (OrderDTO s:allMainOrder) {
            int status1 = Integer.parseInt(params.get("status").toString());
            BigDecimal allpayMoney=new BigDecimal("0");
            List<MasterOrderEntity> auxiliaryOrderByOrderId = baseDao.getAuxiliaryPayOrder(s.getOrderId(),status1);
            if(auxiliaryOrderByOrderId!=null) {
                for (MasterOrderEntity ss : auxiliaryOrderByOrderId) {
                    allpayMoney = allpayMoney.add(ss.getPayMoney());
                }
            }
            Integer status = s.getStatus();
            if(s.getStatus()==Constants.OrderStatus.PAYORDER.getValue()){
                allpayMoney=allpayMoney.add(s.getPayMoney());
            }
            s.setAllpaymoneys(allpayMoney);
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
            for (SlaveOrderEntity order:orderGoods) {
                GoodEntity byid = goodService.getByid(order.getGoodId());
                order.setGoodInfo(byid);
            }
            s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
            s.setSlaveOrder(orderGoods);
            if(s.getRoomId()!=null){
                s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
            }
        }
        return getPageData(allMainOrder,pages.getTotal(), OrderDTO.class);
    }
/***
 *用户端已退款列表
 * @Author: Zhangguanglin
 * @Description:
 * @Date: 2019/10/24
 * @param params:
 * @Return:
 */
    @Override
    public PageData<OrderDTO> selectAgreeRefundOrders(Map<String, Object> params) {
        IPage<MasterOrderEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        params.put("status",Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue());
        List<OrderDTO> allMainOrder = baseDao.getPayOrder(params);

        for (OrderDTO s:allMainOrder) {
            int status1 = Integer.parseInt(params.get("status").toString());
            BigDecimal allpayMoney=new BigDecimal("0");
            List<MasterOrderEntity> auxiliaryOrderByOrderId = baseDao.getAuxiliaryPayOrder(s.getOrderId(),status1);
            if(auxiliaryOrderByOrderId!=null) {
                for (MasterOrderEntity ss : auxiliaryOrderByOrderId) {
                    allpayMoney = allpayMoney.add(ss.getPayMoney());
                }
            }
            Integer status = s.getStatus();
            if(s.getStatus()==Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()){
                allpayMoney=allpayMoney.add(s.getPayMoney());
            }
            s.setAllpaymoneys(allpayMoney);
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
            for (SlaveOrderEntity order:orderGoods) {
                GoodEntity byid = goodService.getByid(order.getGoodId());
                order.setGoodInfo(byid);
            }
            s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
            s.setSlaveOrder(orderGoods);
            if(s.getRoomId()!=null){
                s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
            }
        }
        return getPageData(allMainOrder,pages.getTotal(), OrderDTO.class);
    }

    @Override
    public List<MasterOrderEntity> getAuxiliaryPayOrders(String orderId) {
        return baseDao.getAuxiliaryPayOrders(orderId);
    }

    @Override
    public List<MasterOrderEntity> getAuxiliaryPayOrderss(String orderId) {
        return baseDao.getAuxiliaryPayOrderss(orderId);
    }

    @Override
    public void updateSlaveOrderPointDeduction(BigDecimal mp, BigDecimal pb, String orderId) {
         baseDao.updateSlaveOrderPointDeduction(mp,pb,orderId);
    }

    @Override
    public MasterOrderEntity getOrderByReservationId(long reservationId) {
        return baseDao.getOrderByReservationId(reservationId);
    }

    @Override
    public PageData<MerchantOrderDTO> listMerchantPages(Map<String, Object> params) {
        //int count= baseDao.selectCount(getWrapper(params));
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        String status = params.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            String[] str = status.split(",");
            params.put("statusStr", str);
        }
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
//            boolean contains = merchantId.contains(",");
//            if(contains){
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
//            }else{
//                String[] str = merchantId.split(",");
//                str.
//                params.put("merchantIdStr", str);
//            }

        } else {
            params.put("merchantId", null);
        }
        List<MerchantOrderDTO> list = baseDao.listMerchant(params);
        for (MerchantOrderDTO orderDTO : list) {
            BigDecimal a = orderDTO.getPayMoney();
            if (orderDTO.getStatus()==8){
                a = new BigDecimal("0");
            }
            List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(orderDTO.getOrderId());
            for (MasterOrderEntity orderEntity : masterOrderEntities1) {
                if(orderEntity.getStatus()==Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()){
                    a = a.add(orderEntity.getPayMoney());
                }
            }
//            if (a.compareTo(BigDecimal.ZERO)==0){
//                List<MasterOrderEntity> masterOrderEntities = baseDao.selectPOrderIdByMainOrderID(orderDTO.getOrderId());
//                if(masterOrderEntities.size()!=0){
//                    for (MasterOrderEntity masterOrderEntity : masterOrderEntities) {
//                        if (null != masterOrderEntity.getReservationId() && masterOrderEntity.getReservationId() > 0) {
//                            //同时将包房或者桌设置成未使用状态
//                            merchantRoomParamsSetService.updateStatus(masterOrderEntity.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
//                        }
//                        masterOrderEntity.setCheckStatus(1);
//                        masterOrderEntity.setCheckMode(Constants.CheckMode.MERCHANTCHECK.getValue());
//                        baseDao.updateById(masterOrderEntity);
//                    }
//                }
//            }
            orderDTO.setPayMoney(a);
        }
        return getPageData(list, pages.getTotal(), MerchantOrderDTO.class);
    }
}
