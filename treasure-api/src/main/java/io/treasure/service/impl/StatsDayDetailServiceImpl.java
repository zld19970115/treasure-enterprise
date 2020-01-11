package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.StatsDayDetailDao;
import io.treasure.dto.*;
import io.treasure.entity.*;
import io.treasure.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 平台日交易明细表
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-24
 */
@Service
public class StatsDayDetailServiceImpl extends CrudServiceImpl<StatsDayDetailDao, StatsDayDetailEntity, StatsDayDetailDTO> implements StatsDayDetailService {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantCouponService merchantCouponService;

    @Autowired
    private MasterOrderService masterOrderService;

    @Autowired
    private SlaveOrderService slaveOrderService;

    @Autowired
    private MerchantWithdrawService merchantWithdrawService;

    @Autowired
    private CtDaysTogetherService ctDaysTogetherService;

    @Override
    public QueryWrapper<StatsDayDetailEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");
        QueryWrapper<StatsDayDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        return wrapper;
    }

    @Override
    public int insertFinishUpdate(MasterOrderDTO dto) {
        MasterOrderEntity statsDayDetailEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        StatsDayDetailEntity sdde=new StatsDayDetailEntity();
        sdde.setOrderId(statsDayDetailEntity.getOrderId());
        sdde.setCreateDate(statsDayDetailEntity.getPayDate());
        sdde.setIncidentType(statsDayDetailEntity.getStatus());
        sdde.setPayMobile(statsDayDetailEntity.getContactNumber());
        MerchantEntity merchantById = merchantService.getMerchantById(statsDayDetailEntity.getMerchantId());
        sdde.setPayMerchantName(merchantById.getName());

        BigDecimal num = new BigDecimal("0");
        BigDecimal discountsMoneyByOrderId = slaveOrderService.getDiscountsMoneyByOrderId(statsDayDetailEntity.getOrderId());
        if (discountsMoneyByOrderId == null) {
            discountsMoneyByOrderId = num;
        }
        sdde.setMerchantDiscountAmount(discountsMoneyByOrderId);
        BigDecimal payMoney = statsDayDetailEntity.getPayMoney();
        BigDecimal giftMoney = statsDayDetailEntity.getGiftMoney();
        BigDecimal transactionAmount = payMoney.add(giftMoney);
        BigDecimal orderTotal = transactionAmount.add(discountsMoneyByOrderId);
        sdde.setTransactionAmount(transactionAmount);
        sdde.setOrderTotal(orderTotal);
        sdde.setGiftMoney(statsDayDetailEntity.getGiftMoney());
        sdde.setRealityMoney(statsDayDetailEntity.getPayMoney());
        sdde.setPlatformBrokerage(statsDayDetailEntity.getPlatformBrokerage());
        sdde.setMerchantProceeds(statsDayDetailEntity.getMerchantProceeds());
        sdde.setPayType(statsDayDetailEntity.getPayMode());
        sdde.setPayMerchantId(statsDayDetailEntity.getMerchantId());
        if (statsDayDetailEntity.getPayMode().equals("2")) {
            sdde.setAliPaymoney(statsDayDetailEntity.getPayMoney());
        } else if (statsDayDetailEntity.getPayMode().equals("3")) {
            sdde.setWxPaymoney(statsDayDetailEntity.getPayMoney());
        }
        sdde.setPlatformBalance(masterOrderService.getPlatformBalance());
        BigDecimal num1=new BigDecimal("0.006");
        BigDecimal servicechanrge = payMoney.multiply(num1).setScale(2, BigDecimal.ROUND_HALF_UP);
        sdde.setServiceCharge(servicechanrge);
        int insert = baseDao.insert(sdde);
        ctDaysTogetherService.decideInsertOrUpdate(sdde.getCreateDate(),merchantById.getId(),statsDayDetailEntity.getPayMode(),sdde);
        return insert;
    }

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/12/30
     * @param masterOrderEntity: 生成的主单对象
     * @Return: 生成订单时维护平台日交易表
     */
//    @Override
//    public int creatreStatsDayDetail(MasterOrderEntity masterOrderEntity) {
//        //-------------------------------交易明细维护-----------
//
//        StatsDayDetailEntity sdde = new StatsDayDetailEntity();
//        sdde.setOrderId(masterOrderEntity.getOrderId());
//        sdde.setCreateDate(masterOrderEntity.getCreateDate());
//        sdde.setIncidentType(masterOrderEntity.getStatus());
//        sdde.setPayMobile(masterOrderEntity.getContactNumber());
//        MerchantEntity merchantById = merchantService.getMerchantById(masterOrderEntity.getMerchantId());
//        sdde.setPayMerchantName(merchantById.getName());
//        BigDecimal num = new BigDecimal("0");
////        BigDecimal discountsMoneyByOrderId = slaveOrderService.getDiscountsMoneyByOrderId(masterOrderEntity.getOrderId());
//        if (discountsMoneyByOrderId == null) {
//            discountsMoneyByOrderId = num;
//        }
//        sdde.setMerchantDiscountAmount(discountsMoneyByOrderId);
//        BigDecimal payMoney = masterOrderEntity.getPayMoney();
//        BigDecimal giftMoney = masterOrderEntity.getGiftMoney();
//        BigDecimal transactionAmount = payMoney.add(giftMoney);
//        BigDecimal orderTotal = transactionAmount.add(discountsMoneyByOrderId);
//        sdde.setTransactionAmount(transactionAmount);
//        sdde.setOrderTotal(orderTotal);
//        sdde.setGiftMoney(masterOrderEntity.getGiftMoney());
//        sdde.setRealityMoney(masterOrderEntity.getPayMoney());
//        sdde.setPlatformBrokerage(masterOrderEntity.getPlatformBrokerage());
//        sdde.setMerchantProceeds(masterOrderEntity.getMerchantProceeds());
//        sdde.setPayType(masterOrderEntity.getPayMode());
//        if (masterOrderEntity.getPayMode().equals("2")) {
//            sdde.setAliPaymoney(masterOrderEntity.getPayMoney());
//        } else if (masterOrderEntity.getPayMode().equals("3")) {
//            sdde.setWxPaymoney(masterOrderEntity.getPayMoney());
//        }
//        sdde.setPlatformBalance(masterOrderService.getPlatformBalance());
//
//        int insert = baseDao.insert(sdde);
//        return 1;
//    }

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/12/30
     * @param order: 所退菜品的主订单
     * @param allGoods: 所退菜品对象
     * @Return: 退菜时，维护平台日交易明细表
     */
//    @Override
//    public int insertReturnGood(OrderDTO order, SlaveOrderDTO allGoods) {
//        StatsDayDetailDTO sddd = new StatsDayDetailDTO();
//        sddd.setOrderId(order.getOrderId());
//        sddd.setCreateDate(allGoods.getRefundDate());
//        sddd.setIncidentType(13);
//        MerchantDTO merchantDTO = merchantService.get(order.getMerchantId());
//        sddd.setPayMerchantName(merchantDTO.getName());
//        //订单总价
//        BigDecimal payMoney = allGoods.getPayMoney();
//        BigDecimal freeGold = allGoods.getFreeGold();
//        BigDecimal discountsMoney = allGoods.getDiscountsMoney();
//        BigDecimal add = (freeGold.add(discountsMoney)).add(payMoney);
//        sddd.setOrderTotal(add.negate());
//        sddd.setMerchantDiscountAmount((allGoods.getDiscountsMoney()).negate());
//        sddd.setTransactionAmount((allGoods.getPayMoney().add(allGoods.getFreeGold())).negate());
//        sddd.setGiftMoney((allGoods.getFreeGold()).negate());
//        sddd.setRealityMoney(allGoods.getPayMoney());
//        BigDecimal platformBrokerage = allGoods.getPlatformBrokerage();
//        BigDecimal merchantProceeds = allGoods.getMerchantProceeds();
//        sddd.setPlatformBrokerage(platformBrokerage.negate());
//        sddd.setMerchantProceeds(merchantProceeds.negate());
////        sddd.setPlatformBalance(masterOrderService.getPlatformBalance());
//        String payMode = order.getPayMode();
//        if (payMode.equals(2)) {
//            sddd.setAliPaymoney(allGoods.getPayMoney());
//        } else if (payMode.equals(3)) {
//            sddd.setWxPaymoney(allGoods.getPayMoney());
//        }
//        int insert = baseDao.insert(ConvertUtils.sourceToTarget(sddd, StatsDayDetailEntity.class));
//        return insert;
//    }

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/12/31
     * @param dto:
     * @Return: 同意退款后维护平台日明细表
     */
//    @Override
//    public int insertReturnOrder(MasterOrderDTO dto) {
//        OrderDTO order = masterOrderService.getOrder(dto.getOrderId());
//        StatsDayDetailDTO sddd = new StatsDayDetailDTO();
//        sddd.setCreateDate(dto.getRefundDate());
//        sddd.setIncidentType(8);
//        sddd.setPayMobile(dto.getContactNumber());
//        MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
//        sddd.setPayMerchantName(merchantDTO.getName());
////        BigDecimal merchant_discount_amount = slaveOrderService.getDiscountsMoneyByOrderId(dto.getOrderId());
//        String payMode = dto.getPayMode();
//        BigDecimal payMoney = dto.getPayMoney();
//        BigDecimal giftMoney = dto.getGiftMoney();
//        BigDecimal transaction_amount = payMoney.add(giftMoney);
//        BigDecimal orderTotl = transaction_amount.add(merchant_discount_amount);
//        sddd.setOrderTotal(orderTotl.negate());
//        sddd.setTransactionAmount(transaction_amount.negate());
//        sddd.setMerchantDiscountAmount(merchant_discount_amount.negate());
//        sddd.setRealityMoney(payMoney.negate());
//        sddd.setPlatformBrokerage(order.getPlatformBrokerage().negate());
//        sddd.setMerchantProceeds((order.getMerchantProceeds()).negate());
//        sddd.setPlatformBalance(masterOrderService.getPlatformBalance());
//        if (payMode.equals(2)) {
//            sddd.setAliPaymoney(order.getPayMoney());
//        } else if (payMode.equals(3)) {
//            sddd.setWxPaymoney(order.getPayMoney());
//        }
//
//        int insert = baseDao.insert(ConvertUtils.sourceToTarget(sddd, StatsDayDetailEntity.class));
//        return insert;
//    }

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/12/31
     * @param dto:
     * @Return:取消已支付订单后维护平台日明细表
     */
//    @Override
//    public int insertCancelOrder(MasterOrderDTO dto) {
//        OrderDTO order = masterOrderService.getOrder(dto.getOrderId());
//        StatsDayDetailDTO sddd = new StatsDayDetailDTO();
//        sddd.setCreateDate(dto.getRefundDate());
//        sddd.setIncidentType(8);
//        sddd.setPayMobile(dto.getContactNumber());
//        MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
//        sddd.setPayMerchantName(merchantDTO.getName());
//        BigDecimal merchant_discount_amount = slaveOrderService.getDiscountsMoneyByOrderId(dto.getOrderId());
//        String payMode = dto.getPayMode();
//        BigDecimal payMoney = dto.getPayMoney();
//        BigDecimal giftMoney = dto.getGiftMoney();
//        BigDecimal transaction_amount = payMoney.add(giftMoney);
//        BigDecimal orderTotl = transaction_amount.add(merchant_discount_amount);
//        sddd.setOrderTotal(orderTotl.negate());
//        sddd.setTransactionAmount(transaction_amount.negate());
//        sddd.setMerchantDiscountAmount(merchant_discount_amount.negate());
//        sddd.setRealityMoney(payMoney.negate());
//        sddd.setPlatformBrokerage(order.getPlatformBrokerage().negate());
//        sddd.setMerchantProceeds((order.getMerchantProceeds()).negate());
//        sddd.setPlatformBalance(masterOrderService.getPlatformBalance());
//        if (payMode.equals(2)) {
//            sddd.setAliPaymoney(order.getPayMoney());
//        } else if (payMode.equals(3)) {
//            sddd.setWxPaymoney(order.getPayMoney());
//        }

//        int insert = baseDao.insert(ConvertUtils.sourceToTarget(sddd, StatsDayDetailEntity.class));
//        return insert;
//    }

//    @Override
//    public int insertMerchantWithdraw(long id) {
//        //通过提现id获取提现记录
//        MerchantWithdrawDTO merchantWithdrawDTO = merchantWithdrawService.get(id);
//        //通过提现记录中商户id获取商户信息
//        MerchantDTO merchantDTO = merchantService.get(merchantWithdrawDTO.getMerchantId());
//        StatsDayDetailEntity sdde=new StatsDayDetailEntity();
//        sdde.setCreateDate(merchantWithdrawDTO.getVerifyDate());
//        sdde.setOrderId(String.valueOf(id));
//        sdde.setIncidentType(12);
//        sdde.setPayMerchantName(merchantDTO.getName());
//        //提现表中金额是double转换成bigdecimal
//        BigDecimal money=new BigDecimal(merchantWithdrawDTO.getMoney());
//        sdde.setWithdrawMoney(money);
////        BigDecimal platformBalance = masterOrderService.getPlatformBalance();
////        sdde.setPlatformBalance( platformBalance.subtract(money));
////        Integer type = merchantWithdrawDTO.getType();
////        if(type==1){
////            //微信提现
////            sdde.setWxPaymoney(money);
////        }else if(type==2){
////            //支付宝提现
//            sdde.setAliPaymoney(money);
//        }
//        return baseDao.insert(sdde);
//    }

}