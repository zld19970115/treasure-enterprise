
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
    private MasterOrderService masterOrderService;

    @Autowired
    private MerchantCouponService merchantCouponService;

    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;
    @Autowired
    private PayServiceImpl payService;

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
                if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                    //同时将包房或者桌设置成未使用状态
                    merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                }
                MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
                masterOrderEntity.setStatus(status);
                masterOrderEntity.setCheckStatus(1);
                masterOrderEntity.setCheckMode(Constants.CheckMode.MERCHANTCHECK.getValue());
//                baseDao.updateStatusAndReason(id,status,verify,verify_date,refundReason);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
                }
            } else {
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
    @Transactional(rollbackFor = Exception.class)
    public Result refundYesUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {

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
                        return new Result().error("支付失败！");
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
        if (null != dto) {
            if (dto.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
                baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
                for (SlaveOrderEntity s : slaveOrderEntities) {
                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
                    }
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
        MasterOrderEntity masterOrderEntity = baseDao.selectByOrderId(orderId);
        OrderDTO orderDTO = ConvertUtils.sourceToTarget(masterOrderEntity, OrderDTO.class);
        //商家信息
        MerchantEntity merchantEntity = merchantService.selectById(masterOrderEntity.getMerchantId());
        orderDTO.setMerchantInfo(merchantEntity);
        //加菜信息
        List<MasterOrderEntity> masterOrderEntities = baseDao.selectPOrderId(orderId);
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
//        //菜单信息
//        List<SlaveOrderEntity> slaveOrderEntitys = slaveOrderService.selectByOrderId(orderId);
//        int size=slaveOrderEntitys.size();
//        for (int i = 0; i < size; i++) {
//            SlaveOrderEntity slaveOrderEntity=slaveOrderEntitys.get(i);
//            GoodEntity goodEntity = goodService.selectById(slaveOrderEntity.getGoodId());
//            slaveOrderEntity.setGoodInfo(goodEntity);
//        }
//        orderDTO.setSlaveOrder(slaveOrderEntitys);
        MerchantRoomEntity merchantRoomEntity = merchantRoomService.selectById(masterOrderEntity.getRoomId());
        orderDTO.setMerchantRoomEntity(merchantRoomEntity);
        MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(masterOrderEntity.getReservationId());
        orderDTO.setReservationInfo(merchantRoomParamsSetEntity);
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
//             merchantRoomParamsSetEntity.setState(1);
//             boolean bb=merchantRoomParamsSetService.updateById(merchantRoomParamsSetEntity);
//             if(!bb){
//                 return result.error(-4,"包房/散台预定出错！");
//             }
            } else if (isUse == 1) {
                return result.error(-1, "包房/散台已经预定,请重新选择！");
            }
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
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<MerchantOrderDTO> list = baseDao.listMerchant(params);
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
        int s = masterOrderEntity.getStatus();
        if (s == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
            masterOrderEntity.setStatus(Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue());
            String refundReason = (String) params.get("refundReason");
            if (StringUtils.isNotBlank(refundReason)) {
                masterOrderEntity.setRefundReason(refundReason);
            }
            int i = baseDao.updateById(masterOrderEntity);
            if (i > 0) {
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

        MerchantCouponDTO merchantCouponDTO = merchantCouponService.get(dct.getId());
        Double money = merchantCouponDTO.getDiscount();
        //获取优惠卷金额
        BigDecimal discount = new BigDecimal(money).setScale(2, BigDecimal.ROUND_DOWN);
        //优惠卷辅助运算
        BigDecimal x = new BigDecimal(0);
        /*优惠卷算法*/

        //订单原总价
        BigDecimal totalMoney1 = dct.getTotalMoney();
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
        }else
        {
            return new Result().error("无法取消订单！");
        }
        return new Result();
    }


    /**
     * 更新主订单实付金额
     * @param PayMoney
     * @param orderId
     */
    @Override
    public void updatePayMoney(BigDecimal PayMoney, String orderId) {
        baseDao.updatePayMoney(PayMoney,orderId);
    }
}