package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.ChargeCashDao;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.enm.Constants;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.*;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import io.treasure.utils.OrderUtil;
import io.treasure.utils.SendSMSUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Service
public class ChargeCashServiceImpl extends CrudServiceImpl<ChargeCashDao, ChargeCashEntity, ChargeCashDTO> implements ChargeCashService {
    @Autowired
    ChargeCashSetService chargeCashSetService;
    @Autowired
    SlaveOrderService slaveOrderService;
    @Autowired
    private RecordGiftServiceImpl recordGiftService;
    @Resource
    MasterOrderDao masterOrderDao;
    @Autowired
    private IWXConfig wxPayConfig;
    @Autowired
    StimmeService stimmeService;
    @Autowired
    ClientUserServiceImpl clientUserService;
    @Autowired
    MerchantServiceImpl merchantService;

    @Autowired
    private IWXPay wxPay;

    @Autowired
    MerchantUserService merchantUserService;
    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;

    @Autowired
    private SMSConfig smsConfig;
    @Override
    public QueryWrapper<ChargeCashEntity> getWrapper(Map<String, Object> params) {
        return null;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result orderSave(ChargeCashDTO dto, ClientUserEntity user) throws ParseException {
        Result result = new Result();
        //生成订单号
        String cashOrderId = OrderUtil.getCashOrderIdByTime(user.getId());
        Date d = new Date();
        //保存主订单
        ChargeCashEntity chargeCashEntity = ConvertUtils.sourceToTarget(dto, ChargeCashEntity.class);
        chargeCashEntity.setCashOrderId(cashOrderId);
        chargeCashEntity.setUserId(user.getId());
        chargeCashEntity.setCash(dto.getCash());
        ChargeCashSetEntity chargeCashSetEntity = chargeCashSetService.selectByCash(dto.getCash());
        chargeCashEntity.setChangeGift(chargeCashSetEntity.getChangeGift());
        chargeCashEntity.setCreateDate(d);
        chargeCashEntity.setSaveTime(d);
        chargeCashEntity.setStatus(1);
        int i = baseDao.insert(chargeCashEntity);
        if (i <= 0) {
            return result.error(-2, "没有订单数据！");
        }
        return result.ok(cashOrderId);
    }

    @Override
    public ChargeCashDTO selectByCashOrderId(String cashOrderId) {
        return baseDao.selectByCashOrderId(cashOrderId);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> cashNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);

        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(out_trade_no);
        //        if(masterOrderEntity.getStatus()!=1){
////            mapRtn.put("return_code", "SUCCESS");
////            return mapRtn;
////        }

        if(masterOrderEntity==null||"".equals(masterOrderEntity)){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【没有此订单："+out_trade_no+"】");
            return mapRtn;
        }

        if(masterOrderEntity.getStatus()!= Constants.OrderStatus.NOPAYORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
            System.out.println("wxNotify02===============masterOrderEnitity.getStatus():"+masterOrderEntity.getStatus());
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        //db option flag 1
        if(masterOrderEntity.getStatus()==Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
            if(masterOrderEntity.getReservationId()!=null&&masterOrderEntity.getReservationId()>0){
                merchantRoomParamsSetService.updateStatus(masterOrderEntity.getReservationId(), MerchantRoomEnm.STATE_USE_YEA.getType());
            }
        }
        /****************************************************************************************/
        try{
            if(masterOrderEntity.getPayMoney().compareTo(total_amount)!=0){
                System.out.println("微信支付：支付失败！请联系管理员！【支付金额不一致】");
                throw new Exception("wx_pay_fail:code01");
            }

        }catch(Exception e){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;

        }
        try{
            if(masterOrderEntity==null){
                System.out.println("微信支付：支付失败！请联系管理员！【未找到订单】");
                throw new Exception("wx_pay_fail:code02");
            }
        }catch(Exception e){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }
        /****************************************************************************************/
        if(masterOrderEntity.getStatus()!=1){
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
        masterOrderEntity.setPayMode(Constants.PayMode.WXPAY.getValue());
        masterOrderEntity.setPayDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        Long creator = masterOrderEntity.getCreator();

        List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(masterOrderEntity.getOrderId());
        //System.out.println("position 5 : "+slaveOrderEntities.toString());

        BigDecimal a = new BigDecimal(0);
        for (SlaveOrderEntity slaveOrderEntity : slaveOrderEntities) {
            a = a.add(slaveOrderEntity.getFreeGold());
        }
        ClientUserEntity clientUserEntity = clientUserService.selectById(creator);
        //System.out.println("position 6 : "+clientUserEntity.toString());
        BigDecimal gift = clientUserEntity.getGift();
        System.out.println("wxNotify03==============gift+"+gift);
        gift=gift.subtract(a);
        clientUserEntity.setGift(gift);
        System.out.println("wxNotify04==============gift+"+gift);
        clientUserService.updateById(clientUserEntity);
        Date date = new Date();
        recordGiftService.insertRecordGift2(clientUserEntity.getId(),date,gift,a);
        //System.out.println("position 1 : "+masterOrderDao.toString()+"===reservationType:"+masterOrderEntity.getReservationType());
        //至此
        if(masterOrderEntity.getReservationType()!=Constants.ReservationType.ONLYROOMRESERVATION.getValue()){
            List<SlaveOrderEntity> slaveOrderEntitys=slaveOrderService.selectByOrderId(out_trade_no);
            //System.out.println("position 2 : "+slaveOrderEntitys);
            /****************************************************************************************/
            if(slaveOrderEntitys==null){
                try{
                    System.out.println("支付失败！请联系管理员！【未找到订单菜品】");
                    throw new Exception("wx_pay_fail:code03");
                }catch(Exception e) {
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单菜品】");
                    return mapRtn;
                }
            }else{
                slaveOrderEntitys.forEach(slaveOrderEntity -> {
                    slaveOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                });
                boolean b=slaveOrderService.updateBatchById(slaveOrderEntitys);

                if(!b){
                    try{
                        System.out.println("支付失败！请联系管理员！【更新菜品】");
                        throw new Exception("wx_pay_fail:code04");
                    }catch(Exception e){
                        mapRtn.put("return_code", "FAIL");
                        mapRtn.put("return_msg", "支付失败！请联系管理员！【更新菜品】");
                        return mapRtn;
                    }
                }
            }
        }

        MerchantDTO merchantDto=merchantService.get(masterOrderEntity.getMerchantId());
        //System.out.println("position 3 : "+merchantDto.toString());

        if(null!=merchantDto){
            MerchantUserDTO userDto= merchantUserService.get(merchantDto.getCreator());
            if(null!=userDto){
                String clientId=userDto.getClientId();
                if(StringUtils.isNotBlank(clientId)){
                    AppPushUtil.pushToSingleMerchant("订单管理","您有新的订单，请注意查收！","",userDto.getClientId());
                    StimmeEntity stimmeEntity = new StimmeEntity();
                    Date date1 = new Date();
                    stimmeEntity.setCreateDate(date1);
                    stimmeEntity.setOrderId(masterOrderEntity.getOrderId());
                    stimmeEntity.setType(1);
                    stimmeEntity.setMerchantId(masterOrderEntity.getMerchantId());
                    stimmeEntity.setCreator(masterOrderEntity.getCreator());
                    stimmeService.insert(stimmeEntity);
                }else{

                    try{
                        System.out.println("支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                        throw new Exception("wx_pay_fail:code05");
                    }catch (Exception e){
                        mapRtn.put("return_code", "FAIL");
                        mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                        return mapRtn;
                    }
                }
            }else{

                try{
                    System.out.println("支付失败！请联系管理员！【无法获取商户会员信息】");
                    throw new Exception("wx_pay_fail:code06");
                }catch (Exception e){
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员信息】");
                    return mapRtn;
                }
            }
        }else{
            try{
                System.out.println("支付失败！请联系管理员！【无法获取商户信息】");
                throw new Exception("wx_pay_fail:code07");
            }catch (Exception e){
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户信息】");
                return mapRtn;
            }
        }
        //System.out.println("position 4 : "+masterOrderEntity.toString());


        if(null != merchantDto.getMobile()){
            SendSMSUtil.sendNewOrder(merchantDto.getMobile(),smsConfig);
        }
        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }

}
