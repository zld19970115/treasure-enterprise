
package io.treasure.service.impl;

import cn.hutool.core.convert.Convert;
import com.alipay.api.java_websocket.WebSocket;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.enm.EMessageUpdateType;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.*;
import io.treasure.jra.impl.MerchantMessageJRA;
import io.treasure.jra.impl.MerchantSetJRA;
import io.treasure.jro.MerchantMessage;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import io.treasure.utils.*;
import io.treasure.vo.BackDishesVo;
import io.treasure.vo.OrderVo;
import io.treasure.vo.PageTotalRowData;
import io.treasure.vo.RoomOrderPrinterVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.treasure.enm.EIncrType.ADD;
import static io.treasure.enm.EIncrType.SUB;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class MasterOrderServiceImpl extends CrudServiceImpl<MasterOrderDao, MasterOrderEntity, MasterOrderDTO> implements MasterOrderService {

    @Autowired
    private SMSConfig smsConfig;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantClientService merchantClientService;
    @Autowired
    private SlaveOrderService slaveOrderService;
    @Autowired
    private MerchantRoomService merchantRoomService;
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;
    @Autowired
    private ClientUserService clientUserService;
    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private EvaluateService evaluateService;
    @Autowired
    private MerchantWithdrawService merchantWithdrawService;
    @Autowired
    private GoodService goodService;
    @Autowired
    private StimmeService stimmeService;
    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private MerchantCouponService merchantCouponService;
    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;
    @Autowired
    MerchantMessageJRA merchantMessageJRA;
    @Autowired
    WsPool wsPool;
    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;
    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private MerchantUserService merchantUserService;

    @Autowired
    private StatsDayDetailService statsDayDetailService;

    @Autowired
    private CtDaysTogetherService ctDaysTogetherService;

    @Autowired
    private DistributionRewardServiceImpl distributionRewardService;
    @Autowired
    private UserTransactionDetailsService userTransactionDetailsService;


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
                //baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                //==========================================================================更新排序分类:002
                baseDao.updateStatusAndReasonPlus(id, status, verify, verify_date, refundReason, 2);

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
                    MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
                    SendSMSUtil.sendMerchantReceipt(userDto.getMobile(), merchantDTO.getName(), smsConfig);
                    WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
                    MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(dto.getMerchantId().toString(), EMessageUpdateType.CREATE_ORDER, SUB);
                    wsPool.sendMessageToUser(wsByUser, 1 + "");
                }
            } else {
                return new Result().error("无法接受订单！");
            }
        } else {
            return new Result().error("无法获取订单！");
        }

//        StimmeDTO stimmeDTO = stimmeService.selectByOrderId(dto.getOrderId());
//        stimmeDTO.setStatus(1);//改为已查看
//        stimmeService.update(stimmeDTO);
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

        if (dto.getPayMode().equals("1") && dto.getPayMoney().doubleValue() > 0) {
            ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getCreator());
            UserTransactionDetailsEntity entiry = new UserTransactionDetailsEntity();
            entiry.setCreateDate(new Date());
            entiry.setMobile(clientUserEntity.getMobile());
            entiry.setMoney(dto.getPayMoney());
            entiry.setPayMode(1);
            entiry.setType(2);
            entiry.setBalance(clientUserEntity.getBalance().subtract(dto.getPayMoney()));
            entiry.setUserId(clientUserEntity.getId());
            userTransactionDetailsService.insert(entiry);
        }

        statsDayDetailService.insertFinishUpdate(dto);
        List<MasterOrderDTO> orderByFinance = baseDao.getOrderByFinance(dto.getOrderId());
        for (MasterOrderDTO mod : orderByFinance) {
            statsDayDetailService.insertFinishUpdate(mod);
        }
        Date date = new Date();
        if (null != dto) {
            boolean result1 = this.judgeRockover(dto.getOrderId(), date);
            if (result1) {
                if (dto.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue() && dto.getStatus() != Constants.OrderStatus.NOPAYORDER.getValue() &&
                        dto.getStatus() != Constants.OrderStatus.CANCELNOPAYORDER.getValue() && dto.getStatus() != Constants.OrderStatus.DELETEORDER.getValue() &&
                        dto.getStatus() != Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue() && dto.getStatus() != Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()) {
                    List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(dto.getOrderId());
                    for (SlaveOrderEntity g : orderGoods) {
                        g.setUpdateDate(date);
                        g.setStatus(Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue());
                        SlaveOrderDTO slaveOrderDTO = ConvertUtils.sourceToTarget(g, SlaveOrderDTO.class);
                        slaveOrderService.update(slaveOrderDTO);

                    }
                    dto.setCheckMode(Constants.CheckMode.MERCHANTCHECK.getValue());
                    dto.setCheckStatus(1);
                    dto.setStatus(10);
                    dto.setUpdateDate(date);
                    MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
                    baseDao.updateById(masterOrderEntity);
                    if (dto.getReservationType() == Constants.ReservationType.ONLYROOMRESERVATION.getValue() || dto.getReservationType() == Constants.ReservationType.NORMALRESERVATION.getValue()) {
                        merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                    }
                }
                List<OrderDTO> affiliateOrde = baseDao.getAffiliateOrde(dto.getOrderId());
                for (OrderDTO o : affiliateOrde) {
                    if (o.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue() && o.getStatus() != Constants.OrderStatus.NOPAYORDER.getValue() &&
                            o.getStatus() != Constants.OrderStatus.CANCELNOPAYORDER.getValue() && o.getStatus() != Constants.OrderStatus.DELETEORDER.getValue() &&
                            o.getStatus() != Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue() && o.getStatus() != Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()) {
                        List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(o.getOrderId());
                        for (SlaveOrderEntity g : orderGoods) {
                            g.setUpdateDate(date);
                            g.setStatus(Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue());
                            SlaveOrderDTO slaveOrderDTO = ConvertUtils.sourceToTarget(g, SlaveOrderDTO.class);
                            slaveOrderService.update(slaveOrderDTO);

                        }
                        o.setCheckMode(Constants.CheckMode.MERCHANTCHECK.getValue());
                        o.setCheckStatus(1);
                        o.setStatus(10);
                        o.setUpdateDate(date);
                        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(o, MasterOrderEntity.class);
                        baseDao.updateById(masterOrderEntity);
                        if (o.getReservationType() == Constants.ReservationType.ONLYROOMRESERVATION.getValue() || o.getReservationType() == Constants.ReservationType.NORMALRESERVATION.getValue()) {
                            merchantRoomParamsSetService.updateStatus(o.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                        }
                    }
                }
            } else {
                Result c = new Result();
                c.setCode(1);
                c.setData("翻台失败，您有未处理的订单");
                return c;
            }

        } else {
            return new Result().error("无法获取订单！");
        }
//        //用户支付获得积分，比例暂时为1:1
//        ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getCreator());
//        BigDecimal integral = clientUserEntity.getIntegral();
//        List<MasterOrderEntity> auxiliaryPayOrders = masterOrderService.getAuxiliaryPayOrders(dto.getOrderId());
//        integral = integral.add(dto.getPayMoney());
//        for (MasterOrderEntity auxiliaryPayOrder : auxiliaryPayOrders) {
//            integral = integral.add(auxiliaryPayOrder.getPayMoney());
//        }
//        clientUserEntity.setIntegral(integral);
//        clientUserService.updateById(clientUserEntity);
//        String orderId = dto.getOrderId();
        WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
        System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser
        );
        wsPool.sendMessageToUser(wsByUser, 1 + "");
        BigDecimal distributionReward =new BigDecimal("0");
        //用户支付获得积分，比例暂时为1:1
        ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getCreator());
        BigDecimal integral = clientUserEntity.getIntegral();
        integral = integral.add(dto.getPayMoney());
        distributionReward = distributionReward.add(dto.getPayMoney());
        List<MasterOrderEntity> masterOrderEntities = masterOrderService.selectBYPOrderId(dto.getOrderId());
        for (MasterOrderEntity masterOrderEntity : masterOrderEntities) {
            integral = integral.add(masterOrderEntity.getPayMoney());
            distributionReward = distributionReward.add(masterOrderEntity.getPayMoney());
        }
        clientUserEntity.setIntegral(integral);
        clientUserService.updateById(clientUserEntity);
        BigDecimal total = distributionReward.multiply(new BigDecimal(100));
        distributionRewardService.distribution(clientUserEntity.getMobile(),total.intValue());
        String orderId = dto.getOrderId();
        //粘入内容结束==============================================================================
        List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(orderId);
        for (SlaveOrderEntity slaveOrderEntity : slaveOrderEntities) {
            GoodEntity byid = goodService.getByid(slaveOrderEntity.getGoodId());
            Integer sales = byid.getSales();
            BigDecimal quantity = slaveOrderEntity.getQuantity();
            int i = quantity.intValue();
            int j = sales + i;
            byid.setSales(j);
            goodService.updateById(byid);
        }
        List<MasterOrderEntity> masterOrderEntity = merchantWithdrawService.selectOrderByMartID(dto.getMerchantId());
        MerchantEntity merchantEntity = merchantService.selectById(dto.getMerchantId());
        Double wartCash = merchantWithdrawService.selectWaitByMartId(dto.getMerchantId());
        if (wartCash == null) {
            wartCash = 0.00;
        }
        if (masterOrderEntity == null) {
            if (null != merchantEntity) {
                BigDecimal wartcashZore = new BigDecimal("0.00");
                merchantEntity.setTotalCash(0.00);
                merchantEntity.setAlreadyCash(0.00);
                merchantEntity.setNotCash(0.00);
                merchantEntity.setPointMoney(0.00);
                merchantEntity.setWartCash(wartcashZore);
                merchantService.updateById(merchantEntity);
                return new Result().ok("订单翻台成功！");
            } else {
                return new Result().error("无法获取店铺信息!");
            }
        }
        List<MerchantWithdrawEntity> merchantWithdrawEntities = merchantWithdrawService.selectPoByMartID(dto.getMerchantId());
        if (merchantWithdrawEntities.size() == 0) {
            BigDecimal bigDecimal = merchantWithdrawService.selectTotalCath(dto.getMerchantId());
            BigDecimal bigDecimal1 = merchantWithdrawService.selectPointMoney(dto.getMerchantId());

            BigDecimal wartcashZore = new BigDecimal("0.00");
            if (null == bigDecimal) {
                bigDecimal = new BigDecimal("0.00");
                if (null != merchantEntity) {
                    if (bigDecimal1 == null) {
                        bigDecimal1 = new BigDecimal("0.00");
                    }
                    merchantEntity.setTotalCash(0.00);
                    merchantEntity.setAlreadyCash(0.00);
                    merchantEntity.setNotCash(0.00);
                    merchantEntity.setPointMoney(bigDecimal1.doubleValue());
                    merchantEntity.setWartCash(wartcashZore);
                    merchantService.updateById(merchantEntity);
                    return new Result().ok("订单翻台成功！");
                } else {
                    return new Result().error("无法获取店铺信息!");
                }
            }
            BigDecimal totalCash = bigDecimal.add(bigDecimal1);
            merchantEntity.setTotalCash(totalCash.doubleValue());
            merchantEntity.setAlreadyCash(0.00);
            merchantEntity.setNotCash(bigDecimal.doubleValue());
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcashZore);
            merchantService.updateById(merchantEntity);
            return new Result().ok("订单翻台成功！");
        }
        if (merchantWithdrawEntities.size() != 0) {
            BigDecimal wartcash = new BigDecimal(String.valueOf(wartCash));
            BigDecimal bigDecimal = merchantWithdrawService.selectTotalCath(dto.getMerchantId());//查询总额
            BigDecimal bigDecimal1 = merchantWithdrawService.selectPointMoney(dto.getMerchantId());//查询扣点总额
            if (bigDecimal1 == null) {
                bigDecimal1 = new BigDecimal("0.00");
            }
            if (bigDecimal == null) {
                bigDecimal = new BigDecimal("0.00");
            }
            Double aDouble = merchantWithdrawService.selectAlreadyCash(dto.getMerchantId()); //查询已提现总额
            if (aDouble == null) {
                aDouble = 0.00;
            }
            String allMoney = String.valueOf(merchantWithdrawService.selectByMartId(dto.getMerchantId()));
            BigDecimal v = new BigDecimal(0);
            if (allMoney != "null") {
                v = new BigDecimal(allMoney);
            }
            BigDecimal a = bigDecimal.subtract(v);
            double c = a.doubleValue();
            BigDecimal totalCash = bigDecimal.add(bigDecimal1);
            merchantEntity.setTotalCash(totalCash.doubleValue());
            merchantEntity.setAlreadyCash(aDouble);
            merchantEntity.setNotCash(c);
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcash);
            merchantService.updateById(merchantEntity);
            return new Result().ok("订单翻台成功！");
        }
        return new Result().ok("订单翻台成功！");
    }

    @Override
    @Async
    public Result orderRefundSuccess(String orderNo, int status) {

        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(orderNo);
        masterOrderEntity.setStatus(status);
        masterOrderEntity.setUpdateDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        return new Result().ok(true);
    }

    /**
     * 同意退单(反赠送金)
     *
     * @param id
     * @param status
     * @param verify
     * @param verify_date
     * @param refundReason
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Result refundYesUpdate(long id, int status, long verify, Date verify_date, String refundReason) {
        MasterOrderDTO dto = get(id);
        BigDecimal nu = new BigDecimal("0");
        ClientUserDTO clientUserDTO = clientUserService.get(dto.getCreator());
        String clientId = clientUserDTO.getClientId();
        if (null != dto) {
            List<OrderDTO> affiliateOrde = baseDao.getAffiliateOrde(dto.getOrderId());//查子单

            for (OrderDTO orderDTO : affiliateOrde) {
                if (affiliateOrde.size() == 1 && orderDTO.getReservationType() == Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
                    merchantRoomParamsSetService.updateStatus(orderDTO.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                    orderDTO.setStatus(Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue());
                    baseDao.updateById(ConvertUtils.sourceToTarget(orderDTO, MasterOrderEntity.class));
                    //003===============更新排序状态
                    orderDTO.setResponseStatus(2);//1表示商家已响应
                    baseDao.updateById(ConvertUtils.sourceToTarget(orderDTO, MasterOrderEntity.class));
                    BigDecimal giftMoney = orderDTO.getGiftMoney();
                    BigDecimal num = new BigDecimal("0");
                    if (giftMoney.compareTo(num) == 1) {
                        BigDecimal gift = clientUserDTO.getGift();
                        BigDecimal abc = giftMoney.add(gift).setScale(2, BigDecimal.ROUND_DOWN);
                        clientUserDTO.setGift(abc);
                        clientUserService.update(clientUserDTO);
                    }
                }
            }
            //主单中有房，且仅有房
            if(dto.getReservationType() ==  Constants.ReservationType.ONLYROOMRESERVATION.getValue() && affiliateOrde.size()==0){
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());//释放房间

                dto.setStatus(Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue());
                baseDao.updateById(ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class));
                //003===============更新排序状态
                dto.setResponseStatus(2);//1表示商家已响应
                baseDao.updateById(ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class));
                WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
                MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(dto.getMerchantId().toString(), EMessageUpdateType.REFUND_ORDER, SUB);
                wsPool.sendMessageToUser(wsByUser, 1 + "");
            }

            if (dto.getReservationType() != 2 && dto.getPayMoney().compareTo(nu) == 1) {
                //退款
                Result result1 = payService.refundByOrder(dto.getOrderId(), dto.getPayMoney().toString());

                if (result1.success()) {
                    boolean b = (boolean) result1.getData();
                    if (!b) {
                        return new Result().error("退款失败！");
                    } else {
                        OrderDTO order = masterOrderService.getOrder(dto.getOrderId());
                        BigDecimal giftMoney = order.getGiftMoney();
                        BigDecimal num = new BigDecimal("0");
                        if (giftMoney.compareTo(num) == 1) {
//                    BigDecimal gift = clientUserDTO.getGift();
//                    clientUserDTO.setGift(giftMoney.add(gift));
//                    clientUserService.update(clientUserDTO);
                            BigDecimal gift = clientUserDTO.getGift();
                            BigDecimal abc = giftMoney.add(gift).setScale(2, BigDecimal.ROUND_DOWN);
                            clientUserDTO.setGift(abc);
                            clientUserService.update(clientUserDTO);
                        }
                        if (order.getPOrderId().equals("0") && order.getReservationType() == Constants.ReservationType.ONLYGOODRESERVATION.getValue()) {
                            List<MasterOrderEntity> orderByPOrderId = masterOrderService.getOrderByPOrderId(order.getOrderId());
                            for (MasterOrderEntity s : orderByPOrderId) {
                                if (s.getPOrderId().equals(dto.getOrderId()) && s.getReservationType() == 2) {
                                    merchantRoomParamsSetService.updateStatus(s.getReservationId(), 0);
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
                        WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
                        MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(dto.getMerchantId().toString(), EMessageUpdateType.REFUND_ORDER, SUB);
                        wsPool.sendMessageToUser(wsByUser, 1 + "");
                    }
                } else {
                    return new Result().error(result1.getMsg());
                }
            }
//
//
//
//
//
//
//
//            if (dto.getStatus() != Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
//                return new Result().error("无法退款！");
//            }

        } else {
            return new Result().error("无法获取订单！");
        }
        if (StringUtils.isNoneBlank(clientId)) {
            AppPushUtil.pushToSingleClient("商家同意退单", "您的退单申请已通过审核！", "", clientId);
        }
        MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
        SendSMSUtil.sendAgreeRefund(clientUserDTO.getMobile(), merchantDTO.getName(), smsConfig);
        WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
        System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser
        );
        wsPool.sendMessageToUser(wsByUser, 1 + "");
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
                //baseDao.updateStatusAndReason(id, status, verify, verify_date, refundReason);
                //更新排序004 ----1表示商家已处理
                baseDao.updateStatusAndReasonPlus(id, status, verify, verify_date, refundReason, 2);
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
//                for (SlaveOrderEntity s : slaveOrderEntities) {
//                    if (s.getRefundId() == null || s.getRefundId().length() == 0) {
//                        slaveOrderService.updateSlaveOrderStatus(status, s.getOrderId(), s.getGoodId());
//                    }
//                }
                if (StringUtils.isNoneBlank(clientId)) {
                    AppPushUtil.pushToSingleClient("商家拒绝接单", "您的订单商家已拒绝", "", clientId);
                }
                MerchantEntity merchantById = merchantService.getMerchantById(dto.getMerchantId());
                SendSMSUtil.sendRefuseRefund(clientUserDTO.getMobile(), merchantById.getName(), smsConfig);
                WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
                MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(dto.getMerchantId().toString(), EMessageUpdateType.REFUND_ORDER, SUB);
                wsPool.sendMessageToUser(wsByUser, 1 + "");
            } else {
                return new Result().error("无法退款！");
            }
        } else {
            return new Result().error("无法获取订单！");
        }
        return new Result().ok("拒绝退款成功！");
    }

    @Override
    public Integer selectByPayMode(String orderId) {
        return baseDao.selectByPayMode(orderId);
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

    public int paseIntViaTime(Date target) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String res[] = sdf.format(target).split(":");
        return Integer.parseInt(res[0].trim()) * 60 * 60 + Integer.parseInt(res[1].trim()) * 60 + Integer.parseInt(res[2].trim());
    }

    public boolean timeInRange(Date target, Date start, Date stop) {
        if (paseIntViaTime(target) >= paseIntViaTime(start) && paseIntViaTime(target) <= paseIntViaTime(stop))
            return true;
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result orderSave(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user) throws ParseException {
        Result result = new Result();
        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        Integer reservationType = dto.getReservationType();
        if (dto.getPayfs() != null) {
            baseDao.insertPayMode(orderId, dto.getPayfs());
        }
        if (reservationType != Constants.ReservationType.ONLYGOODRESERVATION.getValue()) {
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(dto.getReservationId());
            Long merchantId = merchantRoomParamsSetEntity.getMerchantId();
            Long roomId = merchantRoomParamsSetEntity.getRoomId();
            long roomSetId = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date a1 = sdf.parse("05:00:00");
            Date b1 = sdf.parse("09:59:00");
            Date a2 = sdf.parse("10:00:00");
            Date b2 = sdf.parse("14:59:00");
            Date a3 = sdf.parse("15:00:00");
            Date b3 = sdf.parse("21:59:00");
            Date a4 = sdf.parse(" 22:00:00");
            Date b4 = sdf.parse(" 23:59:00");
            Date a5 = sdf.parse("00:00:00");
            Date b5 = sdf.parse(" 4:59:00");
            System.out.println(dto.getEatTime());
            System.out.println(a1);
            System.out.println(b1);
            boolean s1 = timeInRange(dto.getEatTime(), a1, b1);
            boolean s2 = timeInRange(dto.getEatTime(), a2, b2);
            boolean s3 = timeInRange(dto.getEatTime(), a3, b3);
            boolean s4 = timeInRange(dto.getEatTime(), a4, b4);
            boolean s5 = timeInRange(dto.getEatTime(), a5, b5);


            if (s1 == true) {
                roomSetId = 1;
            }
            if (s2 == true) {
                roomSetId = 2;
            }
            if (s3 == true) {
                roomSetId = 3;
            }
            if (s4 == true) {
                roomSetId = 4;
            }
            if (s5 == true) {
                roomSetId = 1;
            }
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity1 = merchantRoomParamsSetService.selectByMartIdAndRoomIdAndRoomId(merchantId, roomId, roomSetId, simpleDateFormat.format(dto.getEatTime()));
            if (merchantRoomParamsSetEntity1 == null) {
                return result.error(-5, "没有此包房/散台");
            }
            int isUse = merchantRoomParamsSetEntity1.getState();
            if (isUse == 0) {
                merchantRoomParamsSetEntity1.setState(1);
                //更新状态值
                merchantRoomParamsSetService.updateById(merchantRoomParamsSetEntity1);
            } else if (isUse == 1) {
                return result.error(-1, "包房/散台已经预定,请重新选择！");
            }
        }
        //是否使用优惠卷
        if (dto.getDiscountId() != null) {
            userCouponService.updateStatus(dto.getDiscountId());
        }
        //是否使用赠送金
        if (dto.getGiftMoney() != null && dto.getGiftMoney().doubleValue() > 0) {
            ClientUserEntity clientUserEntity = clientUserService.selectById(user.getId());
            BigDecimal gift = clientUserEntity.getGift();
            BigDecimal useGift = new BigDecimal(dto.getGiftMoney().toString());
            useGift = useGift.setScale(2);
            if (gift.compareTo(useGift) == -1) {
                return result.error(-7, "您的赠送金不足！");
            }
        }
        Date d = new Date();
        //保存主订单
        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setOrderId(orderId);
        masterOrderEntity.setStatus(Constants.OrderStatus.NOPAYORDER.getValue());
        MerchantDTO merchantDTO1 = merchantService.get(masterOrderEntity.getMerchantId());
        MerchantUserDTO merchantUserDTO = merchantUserService.get(merchantDTO1.getCreator());

        //如果包房押金未0，先房后菜情况下设置订单状态未已支付
        if (dto.getReservationType() == 2 && dto.getPayMoney().compareTo(BigDecimal.ZERO) == 0) {
            masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
            MerchantUserEntity merchantUserEntity = merchantUserService.selectByMerchantId(dto.getMerchantId());
            SendSMSUtil.sendNewOrder(merchantUserEntity.getMobile(), smsConfig);
            List<MerchantClientDTO> list = merchantClientService.getMerchantUserClientByMerchantId(merchantUserEntity.getId());
            if (list.size() == 0){
                System.out.println("MasterOrder 849");
            }else {
                for (int i = 0; i < list.size(); i++) {
                    AppPushUtil.pushToSingleMerchant("订单管理", "您有新的订单，请注意查收！",  list.get(i).getClientId());
                }
            }
            //   int i = bitMessageUtil.attachMessage(EMsgCode.ADD_DISHES);
//            System.out.println("i+++++++++++++++++++++++++++++:"+i
//            );
            WebSocket wsByUser = wsPool.getWsByUser(dto.getMerchantId().toString());
            System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser
            );
            MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(dto.getMerchantId().toString(), EMessageUpdateType.CREATE_ORDER, ADD);

            wsPool.sendMessageToUser(wsByUser, merchantMessage.toString());
        }
        MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
        masterOrderEntity.setInvoice("0");
        masterOrderEntity.setCreator(user.getId());
        masterOrderEntity.setCreateDate(d);
        if (reservationType == Constants.ReservationType.ONLYROOMRESERVATION.getValue() && merchantDTO.getDepost() == 0) {
            if (dtoList == null) {
                masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
            }
        }
        BigDecimal a = new BigDecimal("0");
        BigDecimal b = new BigDecimal("0");
        if (dtoList != null) {
            for (SlaveOrderEntity s : dtoList) {
                a = a.add(s.getPlatformBrokerage());
                b = b.add(s.getMerchantProceeds());
            }
        }
        masterOrderEntity.setTotalMoney(masterOrderEntity.getPayMoney());
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
//            MerchantUserEntity merchantUserEntity = merchantUserService.selectByMerchantId(dto.getMerchantId());
//            SendSMSUtil.sendNewOrder(merchantUserEntity.getMobile(), smsConfig);
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
        if (masterOrderService.selectByOrderId(dto.getOrderId()).getCheckStatus() == 1) {
            return result.error(-11, "已翻台订单不可以加菜！");
        }

        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        //是否使用赠送金
        if (dto.getPayfs() != null) {
            baseDao.insertPayMode(orderId, dto.getPayfs());
        }
        if (dto.getGiftMoney() != null && dto.getGiftMoney().doubleValue() > 0) {
            ClientUserEntity clientUserEntity = clientUserService.selectById(user.getId());
            BigDecimal gift = clientUserEntity.getGift();
            BigDecimal useGift = new BigDecimal(dto.getGiftMoney().toString());
            useGift = useGift.setScale(2);
            if (gift.compareTo(useGift) == -1) {
                return result.error(-7, "您的赠送金不足！");
            }
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
        MasterOrderEntity masterOrderEntity1 = baseDao.selectByOrderId(dto.getOrderId());
        masterOrderEntity.setEatTime(masterOrderEntity1.getEatTime());
        masterOrderEntity.setCreator(user.getId());
        masterOrderEntity.setCreateDate(d);
        BigDecimal a = new BigDecimal("0");
        BigDecimal b = new BigDecimal("0");
        if (dtoList != null) {
            for (SlaveOrderEntity s : dtoList) {
                a = a.add(s.getPlatformBrokerage());
                b = b.add(s.getMerchantProceeds());
            }
        }
        masterOrderEntity.setPlatformBrokerage(a);
        masterOrderEntity.setMerchantProceeds(b);
        masterOrderEntity.setResponseStatus(1);  //排序提前
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
//          List<SlaveOrderEntity> slaveOrderEntityList=ConvertUtils.sourceToTarget(dtoList,SlaveOrderEntity.class);
//          boolean b=slaveOrderService.insertBatch(dtoList);
            MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
            MerchantUserEntity merchantUserEntity = merchantUserService.selectByMerchantId(dto.getMerchantId());
            SendSMSUtil.sendNewOrder(merchantUserEntity.getMobile(), smsConfig);
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
        String status = String.valueOf(params.get("status"));
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
        for (MerchantOrderDTO s : list) {
            BigDecimal payMoney = s.getPayMoney();
            BigDecimal giftMoney = s.getGiftMoney();
            s.setPayMoney(payMoney.add(giftMoney));
        }
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

    @Override
    public List<MasterOrderEntity> selectBYPOrderId(String orderId) {
        return baseDao.selectBYPOrderId(orderId);
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
        for (MerchantOrderDTO orderDTO : list) {
            BigDecimal a = orderDTO.getPayMoney();
            BigDecimal giftMoney = orderDTO.getGiftMoney();
            orderDTO.setPayMoney(a.add(giftMoney));
        }
        return getPageData(list, pages.getTotal(), MerchantOrderDTO.class);
    }

    @Override
    public List<MasterOrderEntity> selectByUserId(long userId) {
        return baseDao.selectByUserId(userId);
    }

    @Override
    public List<MasterOrderEntity> selectByMasterId(Map<String, Object> params) {
        return baseDao.selectByMasterId(params);
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
        List<SlaveOrderEntity> sod = slaveOrderService.getOrderGoods(masterOrderEntity.getOrderId());
        for (SlaveOrderEntity s : sod) {
            if (s.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
                return result.error(-1, "您有退菜申请未审核！");
            }
        }
        if (masterOrderEntity.getPOrderId().equals("0")) {
            List<OrderDTO> o = baseDao.selectPOrderIdAndS(masterOrderEntity.getOrderId());
            for (OrderDTO oo : o) {
                if (oo.getStatus() == 4) {
                    return result.error(-1, "您有订单商户未接收！");
                }
            }
        }
        MerchantDTO merchantDTO = merchantService.get(masterOrderEntity.getMerchantId());
        MerchantUserDTO merchantUserDTO = merchantUserService.get(merchantDTO.getCreator());
        String clientId = "";
        List<MerchantClientDTO> list = merchantClientService.getMerchantUserClientByMerchantId(masterOrderEntity.getMerchantId());
        if (list.size() == 0){
            System.out.println("MasterOrder 1176");
        }else {
            clientId = list.get(0).getClientId();
        }
        int s = masterOrderEntity.getStatus();
        if (s == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
            masterOrderEntity.setStatus(Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue());
            String refundReason = (String) params.get("refundReason");
            if (StringUtils.isNotBlank(refundReason)) {
                masterOrderEntity.setRefundReason(refundReason);
            }
            masterOrderEntity.setResponseStatus(1);  //排序提前
            int i = baseDao.updateById(masterOrderEntity);
            if (i > 0) {
                if (StringUtils.isNotBlank(clientId)) {
                    for (int j = 0; j < list.size(); j++) {
                        AppPushUtil.pushToSingleMerchant("订单管理", "您有退款信息，请及时处理退款！",  list.get(j).getClientId());
                    }
                }
                SendSMSUtil.sendApplyRefund(merchantUserDTO.getMobile(), smsConfig);
                WebSocket wsByUser = wsPool.getWsByUser(masterOrderEntity.getMerchantId().toString());
                System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser
                );
                MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(masterOrderEntity.getMerchantId().toString(), EMessageUpdateType.REFUND_ORDER, ADD);

                wsPool.sendMessageToUser(wsByUser, merchantMessage.toString());



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
    //==================================================================================
//    @Test
//    public void test(){
//        BigDecimal b0 = new BigDecimal("22");
//        BigDecimal b1 = new BigDecimal("37");
//        BigDecimal b2 = new BigDecimal("12");
//        BigDecimal b3 = new BigDecimal("15");
//        BigDecimal b4 = new BigDecimal("8");
//
//        List<BigDecimal> bds = new ArrayList<>();
//        bds.add(b0);bds.add(b1);bds.add(b2);bds.add(b3);bds.add(b4);
//
//
//        comboPatch(bds,new BigDecimal("4.7"));
//    }

    //根据各项计算各单项的比值
    public List<BigDecimal> comboPatch(List<BigDecimal> ls, BigDecimal target) {

        List<PatchDto> resultList = new ArrayList<>();
        BigDecimal sum = new BigDecimal("0");

        for (int i = 0; i < ls.size(); i++) {
            sum = sum.add(ls.get(i));     //累计总值
        }
        BigDecimal patchDiff = new BigDecimal("0");
        for (int i = 0; i < ls.size(); i++) {
            BigDecimal itemRes = target.multiply(ls.get(i)).divide(sum, 8, BigDecimal.ROUND_DOWN);

            //System.out.println("原始值："+res);
            resultList.add(new PatchDto(itemRes.setScale(2, BigDecimal.ROUND_DOWN), generateDecimalPart(itemRes), i));//增加位置标识符
            patchDiff = patchDiff.add(itemRes.setScale(2, BigDecimal.ROUND_DOWN));
            //System.out.println("单项均值："+resultList.get(resultList.size()-1).toString());
        }

        int sumInt = Integer.parseInt(patchDiff.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString());
        int targetInt = Integer.parseInt(target.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString());
        //System.out.println("一次计算后的值"+sumInt+","+targetInt);

        if (targetInt > sumInt) {
            //说明比目标值小,需要增加
            Collections.sort(resultList, Collections.reverseOrder());
            System.out.println("均分量不足");

            for (int i = 0; i < (targetInt - sumInt); i++) {
                int ix = i % resultList.size();
                System.out.println("补偿前-" + ix + ":" + resultList.get(ix).toString());
                BigDecimal patchValue = resultList.get(ix).getMainValue().add(new BigDecimal("0.01"));
                resultList.set(ix, new PatchDto(patchValue, resultList.get(ix).getTruncatedDecimal(), resultList.get(ix).getId()));

                System.out.println("补偿编号-" + ix + ":" + resultList.get(ix).toString());
            }
        } else if (targetInt < sumInt) {
            Collections.sort(resultList);
            System.out.println("均分量超限");

            for (int i = 0; i < (sumInt - targetInt); i++) {
                int ix = i % resultList.size();

                if (resultList.get(ix).getMainValue().compareTo(new BigDecimal("0.01")) > 0) {
                    System.out.println("补偿前：" + resultList.get(ix).toString());
                    BigDecimal patchValue = resultList.get(ix).getMainValue().subtract(new BigDecimal("0.01"));
                    System.out.println("补偿后：" + resultList.get(ix).toString());
                    resultList.set(ix, new PatchDto(patchValue, resultList.get(ix).getTruncatedDecimal(), resultList.get(ix).getId()));
                } else {
                    sumInt++;//下次循环再改
                }
            }
        } else {
            List<BigDecimal> res0 = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                res0.add(resultList.get(i).getMainValue());
                System.out.println("最后值:" + res0.get(i));
            }
            return res0;
        }

        List<BigDecimal> res = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            for (int j = 0; j < resultList.size(); j++) {
                if (i == resultList.get(j).getId()) {
                    res.add(resultList.get(j).getMainValue());
                }
            }
            System.out.println("最后值:" + res.get(i));
        }

        return res;
    }

    //根据目标的相应的值得到其中的占比
    public List<BigDecimal> calculatePercentViaPatch(List<BigDecimal> target) {

        BigDecimal sum = new BigDecimal("0");
        BigDecimal percentSum = new BigDecimal("0");        //计算结果百分数累计值
        List<PatchDto> patchDtos = new ArrayList<>();

        for (int i = 0; i < target.size(); i++) {
            sum = sum.add(target.get(i));
        }
        for (int i = 0; i < target.size(); i++) {
            BigDecimal percentItem = target.get(i).divide(sum, 10, BigDecimal.ROUND_HALF_DOWN);

            patchDtos.add(new PatchDto(percentItem.setScale(2, BigDecimal.ROUND_DOWN), generateDecimalPart(percentItem), i));
            percentSum = percentSum.add(percentItem.setScale(2, BigDecimal.ROUND_DOWN));
        }

        int percentInt = Integer.parseInt(percentSum.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString());
        // System.out.println("一次计算后占比百分数percentInt:"+percentInt);
        if (percentInt > 100) {
            Collections.sort(patchDtos);
            for (int i = 0; i < (percentInt - 100); i++) {
                int ix = i % patchDtos.size();

                if (patchDtos.get(ix).getMainValue().compareTo(new BigDecimal("0.01")) > 0) {
                    BigDecimal patchValue = patchDtos.get(ix).getMainValue().subtract(new BigDecimal("0.01"));
                    patchDtos.set(ix, new PatchDto(patchValue, patchDtos.get(ix).getTruncatedDecimal(), patchDtos.get(ix).getId()));
                } else {
                    percentInt++;//下次循环再改
                }

            }
        } else if (percentInt < 100) {
            Collections.sort(patchDtos, Collections.reverseOrder());

//            for(int i=0;i<patchDtos.size();i++){
//                System.out.println("补前"+patchDtos.get(i).toString());
//            }
            for (int i = 0; i < (100 - percentInt); i++) {
                int ix = i % patchDtos.size();

                BigDecimal patchValue = patchDtos.get(ix).getMainValue().add(new BigDecimal("0.01"));
                patchDtos.set(ix, new PatchDto(patchValue, patchDtos.get(ix).getTruncatedDecimal(), patchDtos.get(ix).getId()));

                // System.out.println("补偿编号-"+ix+":"+patchDtos.get(ix).toString());

            }
            BigDecimal xyz = new BigDecimal("0");
            for (int i = 0; i < patchDtos.size(); i++) {
                // System.out.println("补偿后："+patchDtos.get(i).toString());
                xyz = xyz.add(patchDtos.get(i).getMainValue());
            }
            //System.out.println("最后得百分敉："+xyz);
        }
        List<BigDecimal> res = new ArrayList<>();

        for (int i = 0; i < patchDtos.size(); i++) {
            for (int j = 0; j < patchDtos.size(); j++) {
                if (i == patchDtos.get(j).getId()) {
                    res.add(patchDtos.get(j).getMainValue());
                }
            }
            System.out.println("result：" + res.get(i).toString());
        }

        return res;
    }

    //chiguoqiang:生成主体部分与小数标记部分,作为字段排序的依据
    public int generateDecimalPart(BigDecimal bd) {
        String result = null;
        String target = bd.setScale(8, BigDecimal.ROUND_DOWN) + "";
        if (target.contains(".") && target.length() > (target.indexOf(".") + 3))
            return Integer.parseInt(target.substring(target.indexOf(".") + 3));
        return 0;
    }


    //
    //
    //chiguoqiang:优惠选算法（赠送金按项总价，优惠券也按项总价算，需要改时再改）
    public DesignConditionsDTO itemsClculatex(DesignConditionsDTO target, DiscountType discountType) {
        if (target == null) return null;
        List<calculationAmountDTO> slaveOrders = target.getSlaveOrder();

        //维护菜品详细表中赠送金字段，（原价-优惠后单价）*数量=此菜品共优惠多少钱
        BigDecimal priceTotal = target.getTotalMoney();        //原始总价
        BigDecimal priceOrigin = priceTotal;
        BigDecimal priceAfterDiscount = priceTotal;               //优惠后的价格
        //赠送金:假设一单为总价25元，5元的优惠券：优惠后的价格为20元，可用赠送金为20元的10%即2元
        BigDecimal giftRatio = new BigDecimal("0.1");         //赠送金可用比例

        BigDecimal giftValue = new BigDecimal("0");

        BigDecimal oRatio = new BigDecimal("0");
        List<BigDecimal> tmp001 = new ArrayList<>();//f01
        //一次初始化基本参数
        for (int i = 0; i < slaveOrders.size(); i++) {
            //检查商品是否存在，存在则取回
            GoodDTO goodDTO = goodService.get(slaveOrders.get(i).getGoodId());
            calculationAmountDTO slaveOrderItem = slaveOrders.get(i);

            //当商品信息不存在时，保留商品基本信息（其它信息不做处理，留给其它业务2020-04-13）
            if (goodDTO != null)
                slaveOrderItem.setName(goodDTO.getName()).setIcon(goodDTO.getIcon());//更新基本信息

            //项总价
            BigDecimal itemSumPrice = slaveOrderItem.getPrice().multiply(slaveOrderItem.getQuantity().setScale(2, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);

            slaveOrderItem
                    .setNewPrice(slaveOrderItem.getPrice())//新项单价
                    .setTotalMoney(itemSumPrice);//新项总价
            slaveOrderItem.setTotalItemx(itemSumPrice);//初始化优化后的项总价默认值

            slaveOrderItem.setFreeGold(new BigDecimal("0"));
            slaveOrderItem.setDiscountsMoney(new BigDecimal("0"));

            //项总价(用于计算项占比例)
            tmp001.add(itemSumPrice);//单项小计金额
        }
        /*************************/
        //如果不好用需要直接使用值
        List<BigDecimal> ratios = calculatePercentViaPatch(tmp001);        //得到各分项占比
        /*************************/
        //分类处理业务
        switch (discountType.ordinal()) {
            /************************************************************************/
            case 0://优惠券方式和赠送金方式【留位】
            case 1://仅优惠券方式
                /************************************************************************/
                //查询商户优惠券信息（优惠类型：1-金额；2-折扣）
                MerchantCouponDTO merchantCouponDTO = merchantCouponService.get(target.getId());//检查昌否使用了优惠券
//                BigDecimal discountValue= new BigDecimal("0");
//
//                if(merchantCouponDTO != null){
//                    Double discountCardNum = merchantCouponDTO.getDiscount();
//                    BigDecimal discountCardNumBd = new BigDecimal(merchantCouponDTO.getDiscount()).setScale(2,BigDecimal.ROUND_DOWN);
//                    BigDecimal moneyBd = new BigDecimal(merchantCouponDTO.getMoney()).setScale(2,BigDecimal.ROUND_DOWN);
//                    //discountValue  = discountCardNumBd;
//
//                    //discountValue = merchantCouponService.getDiscountCount(target.getTotalMoney(),target.getId());
//                    //======================================================================
//                    //总金额未达到折扣条件
//                    if(target.getTotalMoney().compareTo(moneyBd)<0){
//                        discountValue = new BigDecimal("0");
//                    }else{
//                        if(merchantCouponDTO.getDisType() == 1){
//                            //更新优惠券面值：1-金额类型
//                            if(moneyBd.compareTo(discountCardNumBd)>=0){
//                                discountValue = discountCardNumBd;
//                            }else{
//                                if(target.getTotalMoney().compareTo(discountCardNumBd)<0){
//                                    discountValue = target.getTotalMoney();
//                                }
//                            }
//                        }else{
//                            //折扣：且达到折扣条件9.5代表95折
//                            //更新优惠券面值：2-折扣类型：折扣量(总价X（折扣比例x面值）)
//                            if(discountCardNum<10 && discountCardNum>0){
//                                //折扣类型
//                                BigDecimal discountValueTmp = (new BigDecimal("10").subtract(discountCardNumBd)).multiply(new BigDecimal("0.1")).setScale(4,BigDecimal.ROUND_DOWN);
//                                discountValue = discountValueTmp.multiply(target.getTotalMoney()).setScale(2,BigDecimal.ROUND_DOWN);
//
//                            }else{
//                                discountValue = new BigDecimal("0");
//                            }
//                        }
//                    }
//                    //==================================================================================
//                    //更新优惠后的总金额
//                    priceAfterDiscount = priceTotal.subtract(discountValue).setScale(2,BigDecimal.ROUND_HALF_DOWN);
//
//                }
//                BigDecimal disValue = discountValue;
//                /*************************/
//                //优惠 金额分摊计算=========================================
//                //优惠金额子项更新
//                List<BigDecimal> discounts001=  comboPatch(ratios,disValue);//将此值分摊到各具体项中
//                /*************************/
//
//                if(disValue.compareTo(new BigDecimal("0"))>0){
//
//                    //更新项参数
//                    for(int i = 0; i < slaveOrders.size(); i++){
//                        calculationAmountDTO slaveOrderItem = slaveOrders.get(i);
//                        System.out.println("slaveOrderItem"+slaveOrderItem.getTotalMoney()+","+slaveOrderItem.getQuantity());
//                        //单项总优惠
//                        /*************************/
//                        BigDecimal itemDiscountSum = discounts001.get(i);//;slaveOrderItem.getDiscountsMoney();
//                        //单项优惠后的价格
//                        BigDecimal totalMoneyChange = slaveOrderItem.getTotalMoney().subtract(itemDiscountSum).setScale(2,BigDecimal.ROUND_DOWN);
//
//                        //优惠后的项单价
//                        BigDecimal newPrice = totalMoneyChange.divide(slaveOrderItem.getQuantity(),2,BigDecimal.ROUND_DOWN).setScale(2,BigDecimal.ROUND_DOWN);
//
//                        slaveOrderItem
//                                .setNewPrice(newPrice.setScale(2,BigDecimal.ROUND_DOWN))//优惠后的项单价
//                                .setTotalMoney(totalMoneyChange.setScale(2,BigDecimal.ROUND_DOWN))    //优惠后的项总价
//                                .setDiscountsMoney(itemDiscountSum.setScale(2,BigDecimal.ROUND_DOWN));   //优惠金额=(原项总价-优惠后的项总价)
//                        slaveOrders.set(i,slaveOrderItem);
//                    }
//                }
                if (discountType.ordinal() == 1) {
                    System.out.println("xx当前赠送金的值--原总:" + priceOrigin);
                    slaveOrders = calculateIncomex(slaveOrders);
                    break;
                }

                /************************************************************************/
            case 2://仅赠送金方式
                /************************************************************************/
                //获取用户信息
                //BigDecimal giftValue= new BigDecimal("0");
                ClientUserDTO clientUserDTO = clientUserService.get(target.getUserId());
                if (clientUserDTO != null) {
                    BigDecimal userOwnerGiftValue = clientUserDTO.getGift();

                    //更新赠送金量======
                    giftValue = priceAfterDiscount.multiply(giftRatio).setScale(2, BigDecimal.ROUND_DOWN);
                    //用户赠送金不足
                    if (giftValue.compareTo(userOwnerGiftValue) >= 0) {
                        giftValue = userOwnerGiftValue.setScale(2, BigDecimal.ROUND_DOWN);
                    }
                    /**************************************/
                    //代付金 金额分摊计算=========================================

                    List<BigDecimal> totals = new ArrayList<>();
                    for (int i = 0; i < slaveOrders.size(); i++) {
                        totals.add(slaveOrders.get(i).getTotalMoney());
                    }

                    List<BigDecimal> freeGolds = comboPatch(totals, giftValue);//将此值分摊到各具体项中
                    /**************************************/
                    //更新项参数
                    for (int i = 0; i < slaveOrders.size(); i++) {

                        calculationAmountDTO slaveOrderItem = slaveOrders.get(i);

                        //单项赠送金的值
                        BigDecimal itemGiftValue = freeGolds.get(i);//slaveOrderItem.getFreeGold();
                        //单项优惠赠送金优惠后的价格
                        BigDecimal itemPriceTotalAfterGift = slaveOrderItem.getTotalMoney().subtract(itemGiftValue).setScale(2, BigDecimal.ROUND_DOWN);//.subtract(slaveOrderItem.getDiscountsMoney()).setScale(2,BigDecimal.ROUND_DOWN);
                        BigDecimal newPriceAfterGift = itemPriceTotalAfterGift.divide(slaveOrderItem.getQuantity(), 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);
                        slaveOrderItem
                                .setNewPrice(newPriceAfterGift.setScale(2, BigDecimal.ROUND_DOWN))//使用赠送金后的价格
                                .setTotalMoney(itemPriceTotalAfterGift.setScale(2, BigDecimal.ROUND_DOWN))
                                .setFreeGold(itemGiftValue.setScale(2, BigDecimal.ROUND_DOWN));    //项赠送金使用量
                        slaveOrders.set(i, slaveOrderItem);
                    }
                }

                /************************************************************************/
            case 3://默认不扣减方式
                /************************************************************************/
                System.out.println("xx当前赠送金的值--原总，赠送金:" + priceOrigin + "," + giftValue);
                slaveOrders = calculateIncomex(slaveOrders);

                /************************************************************************/
                break;
        }
        target.setSlaveOrder(slaveOrders);
        return target;
    }
    //==================================================================================

    //chiguoqiang:获取减免金额类型("扣减金额类型：0默认未扣减、1赠送金扣减、2优惠卷扣减、3优惠卷与赠送金同时使用")
    private enum DiscountType {
        DISCOUNT_AND_GIFT,  //赠送金和优惠券
        DISCOUNT_ONLY,      //优惠卷方式
        GIFT_ONLY,          //赠送金方式
        ORIGIN_PRICE;       //默认不扣减
    }

    /**
     * @param slaveOrders
     * @param (-赠送金后的值)
     */
    public List<calculationAmountDTO> calculateIncomex(List<calculationAmountDTO> slaveOrders) {
        BigDecimal platformRatio = new BigDecimal("0.15");           //商家扣点标准(总金额-赠送金，后的15%)

        //收入总额(商家收入+平台收入的总额)
        BigDecimal incomeTotal = new BigDecimal("0");
        //平台应收入总额
        BigDecimal platformTotal = new BigDecimal("0");

        //平台实际收入总金额
        BigDecimal platformTotalReal = new BigDecimal("0");

        List<PatchDto> patchDtos = new ArrayList<>();
        for (int i = 0; i < slaveOrders.size(); i++) {
            calculationAmountDTO slaveOrderItem = slaveOrders.get(i);       //这个值比较准确
            BigDecimal itemSumPrice = slaveOrderItem.getPrice().multiply(slaveOrderItem.getQuantity().setScale(2, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal itemPriceFinal = itemSumPrice.subtract(slaveOrderItem.getDiscountsMoney());//这里是减优惠券

            //平台扣点
            BigDecimal itemIncomePlatform = itemPriceFinal.multiply(platformRatio).setScale(8, BigDecimal.ROUND_DOWN);
            BigDecimal itemIncomeMerchant = itemPriceFinal.subtract(itemIncomePlatform.setScale(2, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
            slaveOrderItem
                    .setMerchantProceeds(itemIncomeMerchant.setScale(2, BigDecimal.ROUND_DOWN))        //商家收入
                    .setPlatformBrokerage(itemIncomePlatform.setScale(2, BigDecimal.ROUND_DOWN));      //平台收入
            slaveOrders.set(i, slaveOrderItem);


            //平台收入补偿
            patchDtos.add(new PatchDto(itemIncomePlatform.setScale(2, BigDecimal.ROUND_DOWN), generateDecimalPart(itemIncomePlatform), i));//用于更正

            //收入总金额累计
            incomeTotal = incomeTotal.add(itemPriceFinal.setScale(2, BigDecimal.ROUND_DOWN));//以此为基准

            //实际收入的钱平台
            platformTotalReal = platformTotalReal.add(itemIncomePlatform.setScale(2, BigDecimal.ROUND_DOWN));//差值参考

        }

        //===========================================================////////////////////////////

        //平台应收总金额
        platformTotal = incomeTotal.multiply(platformRatio).setScale(2, BigDecimal.ROUND_DOWN);
        //平台应收总金额扩大100倍并转成int值
        int incomeTotalInt = Integer.parseInt(platformTotal.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN) + "");

        //平台实际收入总金额
        int platformIncomeRealTotalInt = Integer.parseInt(platformTotalReal.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN) + "");

        if (incomeTotalInt > platformIncomeRealTotalInt) {
            //需要增加加平台扣点的值，余数尾数较大的先补

            Collections.sort(patchDtos, Collections.reverseOrder());

            int steps = incomeTotalInt - platformIncomeRealTotalInt;

            for (int i = 0; i < steps; i++) {
                int loopI = i % patchDtos.size();

                BigDecimal tmp = patchDtos.get(loopI).getMainValue();//平台收入的值

                int index = patchDtos.get(loopI).getId();

                //取出
                calculationAmountDTO cad = slaveOrders.get(index);

                BigDecimal sourcePlatformBrokerage = cad.getPlatformBrokerage();
                BigDecimal sourceMerchantProceeds = cad.getMerchantProceeds();

                if (sourceMerchantProceeds.compareTo(new BigDecimal("0.01")) >= 0) {

                    //更新
                    cad.setPlatformBrokerage(sourcePlatformBrokerage.add(new BigDecimal("0.01")))
                            .setMerchantProceeds(sourceMerchantProceeds.subtract(new BigDecimal("0.01")));

                    slaveOrders.set(index, cad);//送回
                } else {
                    steps++;
                }

            }

        } else if (incomeTotalInt < platformIncomeRealTotalInt) {
            //需要要减少平台扣点的值，余数尾数较小的先补

            Collections.sort(patchDtos);

            int steps = platformIncomeRealTotalInt - incomeTotalInt;

            for (int i = 0; i < steps; i++) {
                int loopI = i % patchDtos.size();

                BigDecimal tmp = patchDtos.get(loopI).getMainValue();

                int index = patchDtos.get(loopI).getId();

                //取出
                calculationAmountDTO cad = slaveOrders.get(index);

                BigDecimal sourcePlatformBrokerage = cad.getPlatformBrokerage();
                BigDecimal sourceMerchantProceeds = cad.getMerchantProceeds();

                if (sourcePlatformBrokerage.compareTo(new BigDecimal("0.01")) >= 0) {

                    //更新
                    cad
                            .setPlatformBrokerage(sourcePlatformBrokerage.subtract(new BigDecimal("0.01")))
                            .setMerchantProceeds(sourceMerchantProceeds.add(new BigDecimal("0.01")));

                    //送回
                    slaveOrders.set(index, cad);
                } else {
                    steps++;
                }

            }
        }
        //============================================================================///////////////////////////

        return slaveOrders;
    }
    //==================================================================================
    //==================================================================================

    /**
     * 计算优惠卷
     */
    public DesignConditionsDTO calculateCoupon(DesignConditionsDTO dct) {
        return itemsClculatex(dct, DiscountType.DISCOUNT_ONLY);
    }

    /**
     * 计算赠送金
     */
    public DesignConditionsDTO calculateGift(DesignConditionsDTO dct) {
        return itemsClculatex(dct, DiscountType.GIFT_ONLY);
    }

    /**
     * 计算赠送金与优惠卷
     */
    public DesignConditionsDTO calculateGiftCoupon(DesignConditionsDTO dct) {
        return itemsClculatex(dct, DiscountType.DISCOUNT_AND_GIFT);
    }

    /**
     * 无任何优惠（只计算扣点）
     */
    @Override
    public DesignConditionsDTO notDiscounts(DesignConditionsDTO dct) {
        return itemsClculatex(dct, DiscountType.ORIGIN_PRICE);
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
            //baseDao.updateStatusAndReason(id, status_new, verify, date, verify_reason);

            //==========================================================================更新排序分类:001
            baseDao.updateStatusAndReasonPlus(id, status_new, verify, date, verify_reason, 2);


            List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
            BigDecimal gif = new BigDecimal("0");
            for (SlaveOrderEntity s : slaveOrderEntities) {
                if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                    slaveOrderService.updateSlaveOrderStatus(status_new, s.getOrderId(), s.getGoodId());
                    gif = gif.add(s.getFreeGold());
                }
            }
            ClientUserDTO clientUserDTO = clientUserService.get(dto.getCreator());
            BigDecimal gift = clientUserDTO.getGift();
            BigDecimal addgif = gift.add(gif);
            clientUserDTO.setGift(addgif);
            clientUserService.update(clientUserDTO);
            if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                //同时将包房或者桌设置成未使用状态
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
            }
            //退款
            BigDecimal money = new BigDecimal("0");
            BigDecimal payMoney = dto.getPayMoney();
            //退款
            if (payMoney.compareTo(money) == 1) {
                Result result1 = payService.refundByOrder(dto.getOrderId(), dto.getPayMoney().toString());
                if (result1.success()) {
                    boolean b = (boolean) result1.getData();
                    if (!b) {
                        return new Result().error("支付失败！");
                    }
                } else {
                    return new Result().error(result1.getMsg());
                }
            }
            ClientUserDTO userDto = clientUserService.get(dto.getCreator());
            if (null != userDto) {
                String clientId = userDto.getClientId();
                if (StringUtils.isNotBlank(clientId)) {
                    //发送个推消息
                    AppPushUtil.pushToSingleClient("商家拒绝接单", "您的订单商家已拒绝", "", clientId);
                }
                MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
                SendSMSUtil.sendMerchantRefusal(userDto.getMobile(), merchantDTO.getName(), smsConfig);

//                int i = bitMessageUtil.attachMessage(EMsgCode.NEW_ORDER);
//                System.out.println("i+++++++++++++++++++++++++++++:"+i
//                );
                WebSocket wsByUser = wsPool.getWsByUser(dto.getCreator().toString());
                MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(dto.getMerchantId().toString(), EMessageUpdateType.CREATE_ORDER, SUB);
                System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser
                );
                wsPool.sendMessageToUser(wsByUser, 1 + "");
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
            BigDecimal a = new BigDecimal("0");
            List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(dto.getOrderId());
            for (SlaveOrderEntity s : slaveOrderEntities) {
                if (s.getRefundId() == null || s.getRefundId().length() == 0) {
                    slaveOrderService.updateSlaveOrderStatus(status_new, s.getOrderId(), s.getGoodId());
                    slaveOrderService.updateSlaveOrderPointDeduction(a, a, s.getOrderId(), s.getGoodId());
                }
            }
            if (null != dto.getReservationId() && dto.getReservationId() > 0) {
                //同时将包房或者桌设置成未使用状态
                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());

            }
            masterOrderService.updateSlaveOrderPointDeduction(a, a, dto.getOrderId());

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
    public Result reserveRoom(OrderDTO dto, ClientUserEntity user, String mainOrderId) throws ParseException {
        Result result = new Result();
        List<MasterOrderEntity> masterOrderEntities = baseDao.selectPOrderIdByMainOrderID(mainOrderId);
        if (masterOrderEntities.size() != 0) {
            return result.error("已预定包房，不可重复预定");
        }
        if (masterOrderService.selectByOrderId(mainOrderId).getCheckStatus() == 1) {
            return result.error(-11, "已翻台订单不可以加房！");
        }
        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        Integer reservationType = dto.getReservationType();
        MerchantRoomParamsSetEntity c = new MerchantRoomParamsSetEntity();
        if (reservationType != Constants.ReservationType.ONLYGOODRESERVATION.getValue()) {
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(dto.getReservationId());
            Long merchantId = merchantRoomParamsSetEntity.getMerchantId();
            Long roomId = merchantRoomParamsSetEntity.getRoomId();
            long roomSetId = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date a1 = sdf.parse("05:00:00");
            Date b1 = sdf.parse("09:59:00");
            Date a2 = sdf.parse("10:00:00");
            Date b2 = sdf.parse("14:59:00");
            Date a3 = sdf.parse("15:00:00");
            Date b3 = sdf.parse("21:59:00");
            Date a4 = sdf.parse(" 22:00:00");
            Date b4 = sdf.parse(" 23:59:00");
            Date a5 = sdf.parse("00:00:00");
            Date b5 = sdf.parse(" 4:59:00");
            System.out.println(dto.getEatTime());
            System.out.println(a1);
            System.out.println(b1);
            boolean s1 = timeInRange(dto.getEatTime(), a1, b1);
            boolean s2 = timeInRange(dto.getEatTime(), a2, b2);
            boolean s3 = timeInRange(dto.getEatTime(), a3, b3);
            boolean s4 = timeInRange(dto.getEatTime(), a4, b4);
            boolean s5 = timeInRange(dto.getEatTime(), a5, b5);


            if (s1 == true) {
                roomSetId = 1;
            }
            if (s2 == true) {
                roomSetId = 2;
            }
            if (s3 == true) {
                roomSetId = 3;
            }
            if (s4 == true) {
                roomSetId = 4;
            }
            if (s5 == true) {
                roomSetId = 1;
            }
            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity1 = merchantRoomParamsSetService.selectByMartIdAndRoomIdAndRoomId(merchantId, roomId, roomSetId, simpleDateFormat.format(dto.getEatTime()));
            if (merchantRoomParamsSetEntity1 == null) {
                return result.error(-5, "没有此包房/散台");
            }
            int isUse = merchantRoomParamsSetEntity1.getState();
            c = merchantRoomParamsSetEntity;
            if (isUse == 0) {
                merchantRoomParamsSetEntity1.setState(1);
                //更新状态值
                merchantRoomParamsSetService.updateById(merchantRoomParamsSetEntity1);
            } else if (isUse == 1) {
                return result.error(-1, "包房/散台已经预定,请重新选择！");
            }
        }
//        if (reservationType == Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
//            MerchantRoomParamsSetEntity merchantRoomParamsSetEntity = merchantRoomParamsSetService.selectById(dto.getReservationId());
//            if (merchantRoomParamsSetEntity == null) {
//                return result.error(-5, "没有此包房/散台");
//            }
//            int isUse = merchantRoomParamsSetEntity.getState();
//            c = merchantRoomParamsSetEntity;
//            if (isUse != 1) {
//                merchantRoomParamsSetService.updateStatus(dto.getReservationId(), 1);
//
//            } else {
//                return result.error(-1, "包房/散台已经预定,请重新选择！");
//            }
//        }
        Date d = new Date();
        //保存主订单
        MasterOrderEntity masterOrderEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        masterOrderEntity.setResponseStatus(1);//排序提前1
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
        OrderDTO order1 = baseDao.getOrder(mainOrderId);
        masterOrderEntity.setEatTime(order1.getEatTime());
        masterOrderEntity.setContacts(order1.getContacts());
        masterOrderEntity.setContactNumber(order1.getContactNumber());
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
        if (orderDTO.getCheckStatus() == 1) {
            return result.error(-11, "已翻台订单不可以加菜！");
        }
        //生成订单号
        String orderId = OrderUtil.getOrderIdByTime(user.getId());
        if (dto.getPayfs() != null) {
            baseDao.insertPayMode(orderId, dto.getPayfs());
        }
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
            }
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
        OrderDTO order = baseDao.getOrder(mainOrderId);
        masterOrderEntity.setEatTime(order.getEatTime());
        masterOrderEntity.setContacts(order.getContacts());
        masterOrderEntity.setContactNumber(order.getContactNumber());
        masterOrderEntity.setResponseStatus(1); //排序提前
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
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        List<OrderDTO> allMainOrder = baseDao.getAllMainOrder(params);
        for (OrderDTO s : allMainOrder) {
            if (s.getPOrderId().equals("0")) {
                EvaluateEntity evaluateEntity = evaluateService.selectByUserIdAndOid(s.getCreator(), s.getOrderId());
                if (evaluateEntity == null) {
                    s.setEvaluateYesOrNo(0);//未评价
                } else {
                    s.setEvaluateYesOrNo(1);//已评价
                }
                boolean b = judgeEvaluate(s.getOrderId());
                if (!b) {
                    s.setCheckStatus(1);
                }
            }
            if(s.getReservationType()==2){
               Integer pOrders =   baseDao.selectPorderIdTypeTwo(s.getOrderId());

               if (pOrders==0 ||pOrders==null ){
                   s.setPOrderYorN(0);//有从单
               }else {
                   s.setPOrderYorN(1);//没有从单
               }
            }


            BigDecimal allpayMoney = new BigDecimal("0");
            List<MasterOrderEntity> auxiliaryOrderByOrderId = baseDao.getAuxiliaryOrderByOrderId(s.getOrderId());
            if (s.getPOrderId().equals("0") && auxiliaryOrderByOrderId.size() == 0) {
                s.setAllpaymoneys(s.getPayMoney());
                List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
                for (SlaveOrderEntity order : orderGoods) {
                    GoodEntity byid = goodService.getByid(order.getGoodId());
                    order.setGoodInfo(byid);
                }
                s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
                s.setSlaveOrder(orderGoods);
                if (s.getRoomId() != null) {
                    s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
                }
            } else {
                if (auxiliaryOrderByOrderId != null) {
                    for (MasterOrderEntity ss : auxiliaryOrderByOrderId) {
                        allpayMoney = allpayMoney.add(ss.getPayMoney());
                    }
                }
                if (s.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()) {
                    allpayMoney = allpayMoney.add(s.getPayMoney());
                }
                s.setAllpaymoneys(allpayMoney);
                List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
                for (SlaveOrderEntity order : orderGoods) {
                    GoodEntity byid = goodService.getByid(order.getGoodId());
                    order.setGoodInfo(byid);
                }
                s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
                s.setSlaveOrder(orderGoods);
                if (s.getRoomId() != null) {
                    s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
                    MerchantRoomParamsSetDTO merchantRoomParamsSetDTO = merchantRoomParamsSetService.get(s.getReservationId());
                    s.setReservationInfo(ConvertUtils.sourceToTarget(merchantRoomParamsSetDTO, MerchantRoomParamsSetEntity.class));
                }
            }
        }
        return getPageData(allMainOrder, pages.getTotal(), OrderDTO.class);
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
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        String orderId = params.get("orderId").toString();
        System.out.println(orderId + "orderId");
        OrderDTO order = masterOrderService.getMasterOrder(orderId);

        List<OrderDTO> allMainOrder = baseDao.getAuxiliaryOrder(params);
        System.out.println(allMainOrder + "allMainOrder");
        allMainOrder.add(order);
        if (allMainOrder.size() != 0) {
            for (OrderDTO s : allMainOrder) {
                System.out.println(s + "s.getOrderId()");
                List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
                for (SlaveOrderEntity og : orderGoods) {
                    og.setGoodInfo(goodService.getByid(og.getGoodId()));
                }
                s.setSlaveOrder(orderGoods);
                s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
                if (s.getRoomId() != null) {
                    s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
                }
            }
        }
        long total = pages.getTotal();
        if (total != 0) {
            total = total + 1;
        }
        return getPageData(allMainOrder, total, OrderDTO.class);
    }

    @Override
    public List<MasterOrderEntity> getStatus4Order(Map<String, Object> params) {
        return baseDao.getStatus4Order(params);
    }


    @Override
    public OrderDTO getMasterOrder(String orderId) {
        return baseDao.getMasterOrder(orderId);
    }

    @Override
    public OrderDTO orderParticulars(String orderId) {
        OrderDTO order = baseDao.getOrder(orderId);
        List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(orderId);
        BigDecimal d = new BigDecimal("0");
        BigDecimal a = new BigDecimal("0");
        for (SlaveOrderEntity og : orderGoods) {
            og.setGoodInfo(goodService.getByid(og.getGoodId()));
            d = d.add(og.getDiscountsMoney());
            BigDecimal price = og.getPrice();
            BigDecimal quantity = og.getQuantity();
            BigDecimal multiply = price.multiply(quantity);
            a = a.add(multiply);
        }
        order.setAccountPaymoney(a);
        order.setSlaveOrder(orderGoods);
        order.setClientUserInfo(clientUserService.getClientUser(order.getCreator()));
        if (order.getRoomId() != null) {
            order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(order.getRoomId()));
        } else if (order.getPOrderId().equals("0")) {
            MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
            if (roomOrderByPorderId != null) {
                order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(roomOrderByPorderId.getRoomId()));
                order.setRoomId(roomOrderByPorderId.getRoomId());
                order.setReservationId(roomOrderByPorderId.getReservationId());
            }
        }
        order.setMerchantInfo(merchantService.getMerchantById(order.getMerchantId()));
        order.setDiscountsMoney(d);

        return order;

    }

    @Override
    public List<OrderDTO> orderParticulars1(String orderId) {
        List<OrderDTO> orders = baseDao.getOrder1(orderId);
        List<OrderDTO> list = baseDao.selectPOrderIdAndS(orderId);
        List<OrderDTO> orderDTOByPorderId = baseDao.getOrderDTOByPorderId(orderId);
        BigDecimal g = new BigDecimal("0");
        BigDecimal d = new BigDecimal("0");
        orders.removeAll(list);
        orders.removeAll(orderDTOByPorderId);
        OrderDTO mainOrder = new OrderDTO();
        BigDecimal AllPayMoney = new BigDecimal("0");
        for (OrderDTO order : orders) {
            if (order.getPOrderId().equals("0") || orders.size() == 1) {
                mainOrder = order;
            }
            if (order.getStatus() != Constants.OrderStatus.NOPAYORDER.getValue() &&
                    order.getStatus() != Constants.OrderStatus.MERCHANTREFUSALORDER.getValue() &&
                    order.getStatus() != Constants.OrderStatus.CANCELNOPAYORDER.getValue() &&
                    order.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue() &&
                    order.getStatus() != Constants.OrderStatus.DELETEORDER.getValue() &&
                    order.getStatus() != Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()) {
                BigDecimal discountsMoney = new BigDecimal("0");
                BigDecimal payMoney = order.getPayMoney();
                BigDecimal giftMoney = order.getGiftMoney();
                AllPayMoney = AllPayMoney.add(payMoney.add(giftMoney));
                List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(order.getOrderId());
                for (SlaveOrderEntity og : orderGoods) {
                    og.setGoodInfo(goodService.getByid(og.getGoodId()));
                    discountsMoney = discountsMoney.add(og.getDiscountsMoney());
                }
                order.setSlaveOrder(orderGoods);
                order.setDiscountsMoney(discountsMoney);
                order.setClientUserInfo(clientUserService.getClientUser(order.getCreator()));
                g = g.add(order.getGiftMoney());
                d = d.add(order.getDiscountsMoney());
                if (order.getRoomId() != null) {
                    order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(order.getRoomId()));
                } else if (order.getPOrderId().equals("0")) {
                    MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
                    if (roomOrderByPorderId != null) {
                        order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(roomOrderByPorderId.getRoomId()));
                        order.setRoomId(roomOrderByPorderId.getRoomId());
                        order.setReservationId(roomOrderByPorderId.getReservationId());
                    }
                }
            }
            mainOrder.setAllPaymoney(AllPayMoney);
            mainOrder.setGiftMoney(g);
            mainOrder.setDiscountsMoney(d);
        }

        return orders;
    }

    @Override
    public List<OrderDTO> getMartOrderInfo(String orderId) {
        List<OrderDTO> orders = baseDao.selectOrder(orderId);
        for (OrderDTO order : orders) {
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(orderId);
            BigDecimal d = new BigDecimal("0");
            for (SlaveOrderEntity og : orderGoods) {
                og.setGoodInfo(goodService.getByid(og.getGoodId()));
                d = d.add(og.getDiscountsMoney());
            }
            order.setSlaveOrder(orderGoods);
            order.setClientUserInfo(clientUserService.getClientUser(order.getCreator()));
            if (order.getRoomId() != null) {
                order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(order.getRoomId()));
            } else if (order.getPOrderId().equals("0")) {
                MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
                if (roomOrderByPorderId != null) {
                    order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(roomOrderByPorderId.getRoomId()));
                    order.setRoomId(roomOrderByPorderId.getRoomId());
                    order.setReservationId(roomOrderByPorderId.getReservationId());
                }
            }
            order.setMerchantInfo(merchantService.getMerchantById(order.getMerchantId()));
            BigDecimal payMoney = order.getPayMoney();
            BigDecimal giftMoney = order.getGiftMoney();
            order.setAllPaymoney(payMoney.add(giftMoney));
            order.setDiscountsMoney(d);
        }

        return orders;
    }

    @Override
    public List<OrderDTO> refundOrder(Map<String, Object> params) {
        String orderId = (String) params.get("orderId");
        String goodIds = (String) params.get("goodId");
        Long goodId = Long.valueOf(goodIds);
        List<OrderDTO> orders = baseDao.getOrder1(orderId);
        List<OrderDTO> list = baseDao.selectPOrderIdAndS(orderId);
        List<OrderDTO> orderDTOByPorderId = baseDao.getOrderDTOByPorderId(orderId);
        orders.removeAll(list);
        orders.removeAll(orderDTOByPorderId);
        for (OrderDTO order : orders) {
            List<SlaveOrderEntity> orderGoods = goodService.getRefundGoods(order.getOrderId(), goodId);
            for (SlaveOrderEntity og : orderGoods) {
                og.setGoodInfo(goodService.getByid(og.getGoodId()));
                BigDecimal giftMoney = og.getFreeGold();
                BigDecimal payMoney = og.getPayMoney();
                og.setPayMoney(giftMoney.add(payMoney));
            }
            order.setSlaveOrder(orderGoods);
            order.setClientUserInfo(clientUserService.getClientUser(order.getCreator()));
            if (order.getRoomId() != null) {
                order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(order.getRoomId()));
            } else if (order.getPOrderId().equals("0")) {
                MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
                if (roomOrderByPorderId != null) {
                    order.setMerchantRoomEntity(merchantRoomService.getmerchantroom(roomOrderByPorderId.getRoomId()));
                    order.setRoomId(roomOrderByPorderId.getRoomId());
                    order.setReservationId(roomOrderByPorderId.getReservationId());
                }
            }
        }
        return orders;
    }

    @Override
    public List<MasterOrderEntity> selectPOrderIdHavePaid(String orderId) {
        return baseDao.selectPOrderIdHavePaid(orderId);
    }

    @Override
    public Result deleteOrder(String orderId) {
        MasterOrderEntity masterOrderEntity = baseDao.selectByOrderId(orderId);
        if (masterOrderEntity == null) {
            return new Result().error("没有找到订单");
        }
        if (masterOrderEntity.getStatus() == 3 || masterOrderEntity.getStatus() == 5 || masterOrderEntity.getStatus() == 8 || masterOrderEntity.getStatus() == 11) {
            List<MasterOrderEntity> masterOrderEntities = baseDao.selectNodelOrders(orderId);
            if (masterOrderEntities.size() != 0) {
                return new Result().error("您有从单未处理，请处理后再试");
            }
            List<MasterOrderEntity> order2 = baseDao.getOrder2(orderId);
            for (MasterOrderEntity masterOrderEntity1 : order2) {
                baseDao.updateOrderDeletedById(masterOrderEntity1.getId());
            }
            return new Result().ok("删除成功");
        } else {
            return new Result().error("该订单不能删除，请稍后再试");
        }


    }

    @Override
    public ShareOrderDTO shareOrder(String orderId) {
        ShareOrderDTO shareOrderDTO = new ShareOrderDTO();
        MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderId);
        if (masterOrderEntity == null) {
            return shareOrderDTO;
        }
        if (masterOrderEntity.getEatTime().getTime() < System.currentTimeMillis()) {
            return shareOrderDTO;
        }
        MerchantEntity merchantEntity = merchantService.selectById(masterOrderEntity.getMerchantId());
        ClientUserEntity clientUserEntity = clientUserService.selectById(masterOrderEntity.getCreator());
        Calendar c = Calendar.getInstance();
        c.setTime(masterOrderEntity.getEatTime());
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        shareOrderDTO.setWeek(dayForWeek);
        shareOrderDTO.setDinnerTime(masterOrderEntity.getEatTime());
        shareOrderDTO.setMerchantEntity(merchantEntity);
        shareOrderDTO.setMobile(clientUserEntity.getMobile());
        shareOrderDTO.setOrderId(orderId);
        if (masterOrderEntity.getContacts() != null) {
            shareOrderDTO.setName(masterOrderEntity.getContacts());
        }
        if (masterOrderEntity.getRoomId() != null) {
            MerchantRoomEntity merchantRoomEntity = merchantRoomService.selectById(masterOrderEntity.getRoomId());
            shareOrderDTO.setRoomCount(merchantRoomEntity.getNumHigh());
            shareOrderDTO.setRoomType(merchantRoomEntity.getType());
            shareOrderDTO.setRoomName(merchantRoomEntity.getName());
            return shareOrderDTO;
        } else {
            List<MasterOrderEntity> masterOrderEntities = baseDao.selectSharePorderid(orderId);
            if (masterOrderEntities.size() == 0) {
                return shareOrderDTO;
            } else {
                for (MasterOrderEntity orderEntity : masterOrderEntities) {
                    MerchantRoomEntity merchantRoomEntity = merchantRoomService.selectById(orderEntity.getRoomId());
                    shareOrderDTO.setRoomCount(merchantRoomEntity.getNumHigh());
                    shareOrderDTO.setRoomType(merchantRoomEntity.getType());
                    shareOrderDTO.setRoomName(merchantRoomEntity.getName());
                }
                return shareOrderDTO;
            }
        }
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
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        params.put("status", Constants.OrderStatus.PAYORDER.getValue());
        List<OrderDTO> allMainOrder = baseDao.getPayOrders(params);

        for (OrderDTO s : allMainOrder) {
            int status1 = Integer.parseInt(params.get("status").toString());
            BigDecimal allpayMoney = new BigDecimal("0");
            List<MasterOrderEntity> auxiliaryOrderByOrderId = baseDao.getAuxiliaryPayOrder(s.getOrderId(), status1);
            if (auxiliaryOrderByOrderId != null) {
                for (MasterOrderEntity ss : auxiliaryOrderByOrderId) {
                    allpayMoney = allpayMoney.add(ss.getPayMoney());
                }
            }
            Integer status = s.getStatus();
            if (s.getStatus() == Constants.OrderStatus.PAYORDER.getValue()) {
                allpayMoney = allpayMoney.add(s.getPayMoney());
            }
            s.setAllpaymoneys(allpayMoney);
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
            for (SlaveOrderEntity order : orderGoods) {
                GoodEntity byid = goodService.getByid(order.getGoodId());
                order.setGoodInfo(byid);
            }
            s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
            s.setSlaveOrder(orderGoods);
            if (s.getRoomId() != null) {
                s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
            }

            if(s.getReservationType()==2){
                Integer pOrders =   baseDao.selectPorderIdTypeTwo(s.getOrderId());

                if (pOrders==0 ||pOrders==null ){
                    s.setPOrderYorN(0);//有从单
                }else {
                    s.setPOrderYorN(1);//没有从单
                }
            }
        }
        return getPageData(allMainOrder, pages.getTotal(), OrderDTO.class);
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
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        params.put("status", Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue());
        List<OrderDTO> allMainOrder = baseDao.getPayOrder(params);

        for (OrderDTO s : allMainOrder) {
            int status1 = Integer.parseInt(params.get("status").toString());
            BigDecimal allpayMoney = new BigDecimal("0");
            List<MasterOrderEntity> auxiliaryOrderByOrderId = baseDao.getAuxiliaryPayOrder(s.getOrderId(), status1);
            if (auxiliaryOrderByOrderId != null) {
                for (MasterOrderEntity ss : auxiliaryOrderByOrderId) {
                    allpayMoney = allpayMoney.add(ss.getPayMoney());
                }
            }
            Integer status = s.getStatus();
            if (s.getStatus() == Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()) {
                allpayMoney = allpayMoney.add(s.getPayMoney());
            }
            s.setAllpaymoneys(allpayMoney);
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(s.getOrderId());
            for (SlaveOrderEntity order : orderGoods) {
                GoodEntity byid = goodService.getByid(order.getGoodId());
                order.setGoodInfo(byid);
            }
            s.setMerchantInfo(merchantService.getMerchantById(s.getMerchantId()));
            s.setSlaveOrder(orderGoods);
            if (s.getRoomId() != null) {
                s.setMerchantRoomEntity(merchantRoomService.getmerchantroom(s.getRoomId()));
            }
        }
        return getPageData(allMainOrder, pages.getTotal(), OrderDTO.class);
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
        baseDao.updateSlaveOrderPointDeduction(mp, pb, orderId);
    }

    @Override
    public MasterOrderEntity getOrderByReservationId(long reservationId) {
        return baseDao.getOrderByReservationId(reservationId);
    }

    @Override
    public List<MasterOrderEntity> getAuxiliaryPayOrder(String orderId, int status) {
        return baseDao.getAuxiliaryPayOrder(orderId, status);
    }

    /**
     * 设置包房
     *
     * @param id
     * @param roomSetId
     */
    @Transient
    @Override
    public Result setRoom(long id, long roomSetId) {
        if (id > 0) {
            if (roomSetId > 0) {
                MasterOrderDTO dto = get(id);
                Long roomId = dto.getRoomId();
                if (null != roomId && roomId > 0) {
                    return new Result().error("订单已预定包房，不允许修改！");
                }
                //查询包房
                MerchantRoomParamsSetDTO roomSetDto = merchantRoomParamsSetService.get(roomSetId);
                if (null != roomSetDto) {
                    roomSetDto.setState(MerchantRoomEnm.STATE_USE_YEA.getType());
                    merchantRoomParamsSetService.update(roomSetDto);
                    dto.setRoomId(roomSetDto.getRoomId());
                } else {
                    return new Result().error("无法获取包房信息！");
                }
                update(dto);
            } else {
                return new Result().error("无法获取预约包房编号！");
            }
        } else {
            return new Result().error("无法获取订单编号！");
        }
        return new Result().ok("设置包房成功！");
    }

    @Override
    public List<OrderDTO> getAffiliateOrde(String orderId) {
        return baseDao.getAffiliateOrde(orderId);
    }

    @Override
    public boolean judgeRockover(String orderId, Date date) {
        List<OrderDTO> allorders = baseDao.getOrder1(orderId);
        boolean has = true;
        boolean hass = true;
        String[] orderStatus = {};
        String[] goodStatus = {};
        String[] orderStatusA = {"4", "6"};
        List<String> strings = new ArrayList<>(Arrays.asList(orderStatus));
        List<String> stringss = new ArrayList<>(Arrays.asList(goodStatus));

        for (OrderDTO o : allorders) {
            String s = o.getStatus().toString();
            strings.add(o.getStatus().toString());
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(o.getOrderId());
            for (SlaveOrderEntity g : orderGoods) {
                stringss.add(g.getStatus().toString());
            }

        }
        HashSet<String> set = new HashSet<>(strings);
        HashSet<String> sets = new HashSet<>(stringss);
        set.retainAll(Arrays.asList(orderStatusA));
        sets.retainAll(Arrays.asList(orderStatusA));
        if (set.size() > 0) {
            has = false;
        }
        if (sets.size() > 0) {
            has = false;
        }
        if (!has || !hass) {
            return false;
        }
        return true;
    }

    @Override
    public boolean judgeEvaluate(String orderId) {
        List<OrderDTO> allorders = baseDao.getOrder1(orderId);
        if (allorders.size() == 1) {
            return false;
        }
        boolean has = true;
        boolean hass = true;
        String[] orderStatus = {};
        String[] goodStatus = {};
        String[] orderStatusA = {"10"};
        List<String> strings = new ArrayList<>(Arrays.asList(orderStatus));
        List<String> stringss = new ArrayList<>(Arrays.asList(goodStatus));

        for (OrderDTO o : allorders) {
            String s = o.getStatus().toString();
            strings.add(o.getStatus().toString());
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(o.getOrderId());
            for (SlaveOrderEntity g : orderGoods) {
                stringss.add(g.getStatus().toString());
            }

        }
        HashSet<String> set = new HashSet<>(strings);
        HashSet<String> sets = new HashSet<>(stringss);
        set.retainAll(Arrays.asList(orderStatusA));
        sets.retainAll(Arrays.asList(orderStatusA));
        if (set.size() > 0) {
            has = false;
        }
        if (sets.size() > 0) {
            has = false;
        }
        if (!has || !hass) {
            return false;
        }
        return true;
    }

    @Override
    public PageData<MerchantOrderDTO> listMerchantPages(Map<String, Object> params) {
        //int count= baseDao.selectCount(getWrapper(params));
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        String status = params.get("status").toString();//"2";
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
            BigDecimal payMoney = orderDTO.getPayMoney();
            BigDecimal giftMoney = orderDTO.getGiftMoney();
            BigDecimal a = payMoney.add(giftMoney);
            if (orderDTO.getStatus() == 8) {
                a = new BigDecimal("0");
            }
            List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(orderDTO.getOrderId());
            for (MasterOrderEntity orderEntity : masterOrderEntities1) {
                if (orderEntity.getStatus() == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue() || orderEntity.getStatus() == Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue() || orderEntity.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
                    BigDecimal giftMoneys = orderEntity.getGiftMoney();
                    BigDecimal payMoneys = orderEntity.getPayMoney();
                    a = a.add(payMoneys.add(giftMoneys));
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

    @Override
    public PageData<MerchantOrderDTO> listMerchantPagesPC(Map<String, Object> params) {
        IPage<MasterOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        String status = params.get("status").toString();//"2";
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
        List<MerchantOrderDTO> list = baseDao.listMerchantPC(params);
        for (MerchantOrderDTO orderDTO : list) {
            BigDecimal payMoney = orderDTO.getPayMoney();
            BigDecimal giftMoney = orderDTO.getGiftMoney();
            BigDecimal a = payMoney.add(giftMoney);
            if (orderDTO.getStatus() == 8) {
                a = new BigDecimal("0");
            }
            List<MasterOrderEntity> masterOrderEntities1 = baseDao.selectBYPOrderId(orderDTO.getOrderId());
            for (MasterOrderEntity orderEntity : masterOrderEntities1) {
                if (orderEntity.getStatus() == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue() || orderEntity.getStatus() == Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue() || orderEntity.getStatus() == Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()) {
                    BigDecimal giftMoneys = orderEntity.getGiftMoney();
                    BigDecimal payMoneys = orderEntity.getPayMoney();
                    a = a.add(payMoneys.add(giftMoneys));
                }
            }
            orderDTO.setPayMoney(a);
        }
        return getPageData(list, pages.getTotal(), MerchantOrderDTO.class);
    }

    @Override
    public BigDecimal getPlatformBalance() {
        return baseDao.getPlatformBalance();
    }

    @Override
    public PageData<BackDishesVo> backDishesPage(Map map) {
        PageHelper.startPage(Integer.parseInt(map.get("page") + ""), Integer.parseInt(map.get("limit") + ""));
        Page<BackDishesVo> page = (Page) baseDao.backDishesPage(map);
        return new PageData<BackDishesVo>(page.getResult(), page.getTotal());

    }

    @Override
    public List<MasterOrderEntity> selectByMasterIdAndStatus(long martId) {
        return baseDao.selectByMasterIdAndStatus(martId);
    }

    @Override
    public PageTotalRowData<OrderVo> pagePC(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page") + ""), Integer.parseInt(params.get("limit") + ""));
        Page<OrderVo> page = (Page) baseDao.pagePC(params);
        Map map = new HashMap();
        if (page.getResult() != null && page.getResult().size() > 0) {
            OrderVo vo = baseDao.pagePCTotalRow(params);
            if (vo != null) {
                map.put("totalMoney", vo.getTotalMoney());
                map.put("giftMoney", vo.getGiftMoney());
                map.put("payMoney", vo.getPayMoney());
                map.put("merchantProceeds", vo.getMerchantProceeds());
                map.put("platformBrokerage", vo.getPlatformBrokerage());
            }
        }
        return new PageTotalRowData<>(page.getResult(), page.getTotal(), map);
    }

    @Override
    public RoomOrderPrinterVo roomOrderPrinter(String orderId) {
        RoomOrderPrinterVo vo = baseDao.roomOrderPrinter(orderId);
        vo.setGoodList(baseDao.goodPrinter(vo.getOrderId()));
        return vo;
    }

    @Override
    public List<MasterOrderEntity> selectInProcessList(long martId){
        return masterOrderDao.selectInProcessList(martId);
    }
}

/*
//根据目标的相应的值得到其中的占比
    public List<BigDecimal> calculatePercentx(List<BigDecimal> target){


        BigDecimal sum = new BigDecimal("0");
        BigDecimal stepBase = new BigDecimal("0.01");
        BigDecimal patchValue = new BigDecimal("0");

        BigDecimal percentSum = new BigDecimal("0");//计算结果百分数
        List<BigDecimal> percentList = new ArrayList<>();
        for(int i=0;i<target.size();i++){
            sum = sum.add(target.get(i));
        }
        for(int i=0;i<target.size();i++){
            percentList.add(target.get(i).divide(sum,2,BigDecimal.ROUND_DOWN));
            percentSum = percentSum.add(percentList.get(i).setScale(2,BigDecimal.ROUND_HALF_DOWN));
        }

        int percentInt = Integer.parseInt(percentSum.multiply(new BigDecimal("100")).setScale(0,BigDecimal.ROUND_DOWN).toString());
        if(percentInt >100){
            for(int i=0;i<(percentInt-100);i++){

                if(percentList.get(i%percentList.size()).compareTo(new BigDecimal("0.01"))<0){
                    percentInt++;
                }else{

                    int ix = i%percentList.size();
                    BigDecimal itemTmp = percentList.get(ix).subtract(new BigDecimal("0.01"));
                    percentList.set(ix,itemTmp);
                }
            }
        }else if(percentInt <100){
            for(int i=0;i<(100-percentInt);i++){
                BigDecimal itemTmp = percentList.get(i).add(new BigDecimal("0.01"));
                percentList.set(i,itemTmp);
            }
        }
        return percentList;
    }
    //根据比例分摊各自的值
    public List<BigDecimal> proratex(List<BigDecimal> reference,BigDecimal target){
        List<BigDecimal> resultList = new ArrayList<>();
        BigDecimal sum = new BigDecimal("0");
        for(int i=0;i<reference.size();i++){
            BigDecimal res = reference.get(i).multiply(target).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            resultList.add(res);
            sum=sum.add(res);
        }

        int sumInt = Integer.parseInt(sum.multiply(new BigDecimal("100")).setScale(0,BigDecimal.ROUND_DOWN).toString());
        int targetInt = Integer.parseInt(target.multiply(new BigDecimal("100")).setScale(0,BigDecimal.ROUND_DOWN).toString());
        if(target.compareTo(sum)>0){
            //说明比目标值小,需要增加
            int diffValue = targetInt-sumInt;
            for(int i=0;i<(targetInt-sumInt);i++){
                int ix = i%resultList.size();
                if(reference.get(ix).compareTo(new BigDecimal("0.00"))>0){

                    if(diffValue > 0){
                        BigDecimal tmp = resultList.get(ix).add(new BigDecimal("0.01"));
                        resultList.set(ix,tmp);
                        diffValue--;
                    }

                }else{
                    sumInt--;
                }

            }

        }else if(target.compareTo(sum)<0){
            //比目标值大，需要减少
            for(int i=0;i<(sumInt-targetInt);i++){
                int ix = i%resultList.size();
                BigDecimal tmp = new BigDecimal("0");
                if(resultList.get(ix).compareTo(new BigDecimal("0.03"))>=0){
                    tmp = resultList.get(ix).subtract(new BigDecimal("0.01"));
                    resultList.set(ix,tmp);
                }else{
                    sumInt++;
                }
            }
        }
        return resultList;
    }
 */