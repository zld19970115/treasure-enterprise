package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.dao.StatsDayDetailDao;
import io.treasure.dto.*;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.entity.StatsDayDetailEntity;
import io.treasure.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
        Integer reservationType = dto.getReservationType();
        Integer status = dto.getStatus();
        String refundId = dto.getRefundId();
        MerchantEntity merchantById = merchantService.getMerchantById(dto.getMerchantId());
        //如果主订单为房单
        if(reservationType==2){
            StatsDayDetailEntity sdde=new StatsDayDetailEntity();
            sdde.setOrderId(dto.getOrderId());
            sdde.setPayMerchantName(merchantById.getName());
            sdde.setPayMobile(dto.getContactNumber());
            sdde.setPayMerchantId(merchantById.getId());
            sdde.setIncidentType(dto.getStatus());
            return baseDao.insert(sdde);
        }
        //如果是把菜品全退了改变的所属定单状态
        if(null == refundId && status==8){
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(dto.getOrderId());
            for (SlaveOrderEntity soe:orderGoods) {
                if(null!=soe.getRefundId()){
                    StatsDayDetailEntity sdde=new StatsDayDetailEntity();
                    sdde.setCreateDate(dto.getPayDate());
                    sdde.setOrderId(soe.getRefundId());
                    sdde.setPayMerchantId(dto.getMerchantId());
                    sdde.setPayMerchantName(merchantById.getName());
                    sdde.setPayMobile(dto.getContactNumber());
                    sdde.setPayType(soe.getStatus().toString());
                    BigDecimal payMoney = soe.getPayMoney();
                    BigDecimal freeGold = soe.getFreeGold();
                    BigDecimal discountsMoney = soe.getDiscountsMoney();
                    BigDecimal add = payMoney.add(freeGold);
                    sdde.setOrderTotal((add.add(discountsMoney)).negate());

                    sdde.setTransactionAmount((payMoney.add(freeGold)).negate());
                    sdde.setRealityMoney(payMoney.negate());
                    sdde.setGiftMoney(freeGold.negate());
                    sdde.setMerchantDiscountAmount((soe.getDiscountsMoney()).negate());
                    BigDecimal num1=new BigDecimal("0.006");
                    BigDecimal servicechanrge = payMoney.multiply(num1).setScale(2, BigDecimal.ROUND_HALF_UP);
                    sdde.setServiceCharge(servicechanrge.negate());
                    if(null != dto.getPayMode()){
                        if (dto.getPayMode().equals("2")) {
                            sdde.setAliPaymoney((dto.getPayMoney()).negate());
                            sdde.setPayType("2");
                        } else if (dto.getPayMode().equals("3")) {
                            sdde.setWxPaymoney((dto.getPayMoney()).negate());
                            sdde.setPayType("3");
                        } else if (dto.getPayMode().equals("1")) {
                            sdde.setYePaymoney((dto.getPayMoney()).negate());
                            sdde.setPayType("1");
                        }
                    }
                    return baseDao.insert(sdde);
                }

            }
        }
        //如果主单是直接退单改变的状态
        if(null!=refundId && status==8){
            BigDecimal totalMoney = dto.getTotalMoney();
            BigDecimal giftMoney = dto.getGiftMoney();
            BigDecimal payMoney = dto.getPayMoney();
            StatsDayDetailEntity sdde=new StatsDayDetailEntity();
            sdde.setCreateDate(dto.getPayDate());
            sdde.setOrderId(refundId);
            sdde.setPayMerchantId(dto.getMerchantId());
            sdde.setPayMerchantName(merchantById.getName());
            sdde.setRealityMoney((dto.getPayMoney()).negate());
            sdde.setGiftMoney(giftMoney.negate());
            sdde.setYePaymoney(dto.getPayCoins().negate());
            BigDecimal discountsMoneyByOrderId = slaveOrderService.getDiscountsMoneyByOrderId(dto.getOrderId());
            BigDecimal add = totalMoney.add(giftMoney);
            sdde.setOrderTotal((add.add(discountsMoneyByOrderId)).negate());
            sdde.setMerchantDiscountAmount(discountsMoneyByOrderId.negate());
            sdde.setTransactionAmount((totalMoney.add(giftMoney)).negate());
            BigDecimal num1=new BigDecimal("0.006");
            BigDecimal servicechanrge = payMoney.multiply(num1).setScale(2, BigDecimal.ROUND_HALF_UP);
            sdde.setServiceCharge(servicechanrge.negate());
            sdde.setIncidentType(dto.getStatus());
            if(null != dto.getPayMode()){
                if (dto.getPayMode().equals("2")) {
                    sdde.setAliPaymoney(dto.getPayMoney());
                    sdde.setPayType("2");
                } else if (dto.getPayMode().equals("3")) {
                    sdde.setWxPaymoney(dto.getPayMoney());
                    sdde.setPayType("3");
                }else if (dto.getPayMode().equals("1")) {
                    sdde.setYePaymoney((dto.getPayMoney()).negate());
                    sdde.setPayType("1");
                }
            }
            return  baseDao.insert(sdde);
        }
        MasterOrderEntity statsDayDetailEntity = ConvertUtils.sourceToTarget(dto, MasterOrderEntity.class);
        StatsDayDetailEntity sdde=new StatsDayDetailEntity();
        sdde.setOrderId(statsDayDetailEntity.getOrderId());
        sdde.setCreateDate(statsDayDetailEntity.getPayDate());
        sdde.setIncidentType(statsDayDetailEntity.getStatus());
        sdde.setPayMobile(statsDayDetailEntity.getContactNumber());
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
        sdde.setYePaymoney(statsDayDetailEntity.getPayCoins());
        if(null != statsDayDetailEntity.getPayMode()){
            if (statsDayDetailEntity.getPayMode().equals("2")) {
                sdde.setAliPaymoney(statsDayDetailEntity.getPayMoney());
            } else if (statsDayDetailEntity.getPayMode().equals("3")) {
                sdde.setWxPaymoney(statsDayDetailEntity.getPayMoney());
            }else if (statsDayDetailEntity.getPayMode().equals("1")) {
                sdde.setYePaymoney(statsDayDetailEntity.getPayMoney());

            }
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
    @Override
    public int insertReturnOrder(MasterOrderDTO dto) {
        OrderDTO order = masterOrderService.getOrder(dto.getOrderId());
        MerchantEntity merchantById = merchantService.getMerchantById(order.getMerchantId());
        StatsDayDetailEntity sddd = new StatsDayDetailEntity();
        sddd.setCreateDate(dto.getPayDate());
        if(dto.getRefundId()!=null){
            sddd.setOrderId(dto.getRefundId());
        }else{
            sddd.setOrderId(dto.getOrderId());
        }
        sddd.setIncidentType(8);
        sddd.setPayMobile(dto.getContactNumber());
        MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
        sddd.setPayMerchantName(merchantDTO.getName());
        BigDecimal merchant_discount_amount = slaveOrderService.getDiscountsMoneyByOrderId(dto.getOrderId());
        String payMode = dto.getPayMode();
        BigDecimal payMoney = dto.getPayMoney();
        BigDecimal giftMoney = dto.getGiftMoney();
        BigDecimal transaction_amount = payMoney.add(giftMoney);
        BigDecimal orderTotl = transaction_amount.add(merchant_discount_amount);
        sddd.setOrderTotal(orderTotl.negate());
        sddd.setTransactionAmount(transaction_amount.negate());
        sddd.setMerchantDiscountAmount(merchant_discount_amount.negate());
        sddd.setRealityMoney(payMoney.negate());
        sddd.setPlatformBrokerage(order.getPlatformBrokerage().negate());
        sddd.setMerchantProceeds((order.getMerchantProceeds()).negate());
        sddd.setPlatformBalance(masterOrderService.getPlatformBalance().negate());
        sddd.setPayType(payMode);
        sddd.setGiftMoney((order.getGiftMoney()).negate());
        sddd.setYePaymoney((order.getPayCoins()).negate());
        sddd.setPayMerchantId(order.getMerchantId());
        if (payMode.equals("2")) {
            sddd.setAliPaymoney((order.getPayMoney()).negate());
        } else if (payMode.equals("3")) {
            sddd.setWxPaymoney((order.getPayMoney()).negate());
        }else if (payMode.equals("1")) {
            sddd.setYePaymoney((order.getPayMoney()).negate());
        }
        BigDecimal num1=new BigDecimal("0.006");
        BigDecimal servicechanrge = payMoney.multiply(num1).setScale(2, BigDecimal.ROUND_HALF_UP);
        sddd.setServiceCharge(servicechanrge.negate());
        int insert = baseDao.insert(sddd);
        ctDaysTogetherService.decideInsertOrUpdate(sddd.getCreateDate(),merchantById.getId(),order.getPayMode(),sddd);
        return insert;
    }

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

    /**
     * @param id 提现操作
     * @return
     */
    @Override
    public int insertMerchantWithdraw(long id) {
        //通过提现id获取提现记录
        MerchantWithdrawDTO merchantWithdrawDTO = merchantWithdrawService.get(id);
        //通过提现记录中商户id获取商户信息
        MerchantDTO merchantDTO = merchantService.get(merchantWithdrawDTO.getMerchantId());
        StatsDayDetailEntity sdde=new StatsDayDetailEntity();
        sdde.setCreateDate(merchantWithdrawDTO.getVerifyDate());
        sdde.setOrderId(String.valueOf(id));
        sdde.setIncidentType(12);
        sdde.setPayMerchantName(merchantDTO.getName());
        //提现表中金额是double转换成bigdecimal
        BigDecimal money=new BigDecimal(merchantWithdrawDTO.getMoney());
        sdde.setWithdrawMoney(money);
          BigDecimal platformBalance = masterOrderService.getPlatformBalance();
          sdde.setPlatformBalance( platformBalance.subtract(money));
          Integer type = merchantWithdrawDTO.getType();
          if(type==1){
              //微信提现
              sdde.setWxPaymoney(money);
          }else if(type==2){
              //支付宝提现
            sdde.setAliPaymoney(money);
        }
        return baseDao.insert(sdde);
    }

    @Override
    public int orderCount(String orderId) {
        return 0;
    }

    @Override
    public int insertRefundGood(MasterOrderDTO dto) {
        MasterOrderDTO dtos=dto;
        int num=0;
        if(dto.getStatus()!=8 && dto.getReservationType()!=2){
            num = this.insertFinishUpdate(dto);

        }else{
            if(null==dto.getRefundId()){
                List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(dto.getOrderId());
                for (SlaveOrderEntity soe:orderGoods) {
                    BigDecimal payMoney = soe.getPayMoney();
                    BigDecimal giftMoney = soe.getFreeGold();
                    BigDecimal totalMoney = soe.getTotalMoney();
                    BigDecimal discountsMoney = soe.getDiscountsMoney();
                    StatsDayDetailEntity sdde=new StatsDayDetailEntity();
                    sdde.setPayMerchantId(dto.getMerchantId());
                    MerchantDTO merchantDTO = merchantService.get(dto.getMerchantId());
                    sdde.setPayMerchantName(merchantDTO.getName());
                    sdde.setGiftMoney(giftMoney.negate());
                    BigDecimal paymoneyall = payMoney.add(giftMoney);
                    sdde.setOrderTotal(paymoneyall.negate());
                    sdde.setRealityMoney(payMoney.negate());
                    sdde.setMerchantDiscountAmount(discountsMoney.negate());
                    sdde.setTransactionAmount(payMoney.negate());
                    if(null!=soe.getRefundId()){
                        sdde.setOrderId(soe.getRefundId());
                    }
                    sdde.setPayMobile(dto.getContactNumber());
                    sdde.setCreateDate(dto.getRefundDate());
                    sdde.setIncidentType(8);
                    String payMode = dto.getPayMode();
                    if(payMode.equals("2")){
                        sdde.setPayType("2");
                        sdde.setAliPaymoney(totalMoney.negate());
                    }else if(payMode.equals("3")){
                        sdde.setPayType("3");
                        sdde.setWxPaymoney(totalMoney.negate());
                    }else if(payMode.equals("1")){
                        sdde.setPayType("1");
                        sdde.setYePaymoney(totalMoney.negate());
                    }

                    num = baseDao.insert(sdde);
                }
            }else{
                num = this.insertReturnOrder(dto);
            }

        }
        return num;
    }

}