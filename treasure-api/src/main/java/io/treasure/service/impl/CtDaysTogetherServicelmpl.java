package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.CtDaysTogetherDao;
import io.treasure.dto.CtDaysTogetherDTO;
import io.treasure.entity.CtDaysTogetherEntity;
import io.treasure.entity.StatsDayDetailEntity;
import io.treasure.service.CtDaysTogetherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/***
 * @Author: Zhangguanglin
 * @Description:
 * @Date: 2020/1/10
 * @param :
 * @Return: 平台商户天合计
 */
@Service
public class CtDaysTogetherServicelmpl  extends CrudServiceImpl<CtDaysTogetherDao, CtDaysTogetherEntity, CtDaysTogetherDTO> implements CtDaysTogetherService {
    @Override
    public QueryWrapper<CtDaysTogetherEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");
        QueryWrapper<CtDaysTogetherEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        return wrapper;
    }


    @Override
    public CtDaysTogetherEntity getDateAndMerid(Date date, long merchantId,String type) {
        return baseDao.getDateAndMerid(date,merchantId,type);
    }

    @Override
    public int decideInsertOrUpdate(Date date,long merchantId,String type,StatsDayDetailEntity sdde) {
        CtDaysTogetherEntity cdt = this.getDateAndMerid(date, merchantId,type);
        BigDecimal wxPaymoney = sdde.getWxPaymoney();
        BigDecimal aliPaymoney = sdde.getAliPaymoney();
        BigDecimal YePaymoney = sdde.getYePaymoney();
        BigDecimal realityMoney1 = sdde.getRealityMoney();
        BigDecimal num=new BigDecimal("0");
        int c=0;
        if(realityMoney1.compareTo(num)!=-1){
        if(null !=cdt){
            BigDecimal orderTotal = cdt.getOrderTotal();
            BigDecimal merchantProceeds = cdt.getMerchantProceeds();
            BigDecimal platformBrokerage = cdt.getPlatformBrokerage();
            BigDecimal serviceChanrge = cdt.getServiceChanrge();
            BigDecimal realityMoney = cdt.getRealityMoney();
            BigDecimal merchantDiscountAmount = cdt.getMerchantDiscountAmount();
            BigDecimal giftMoney = cdt.getGiftMoney();



            BigDecimal neworderTotal = orderTotal.add(sdde.getOrderTotal());
            BigDecimal newmerchantProceeds = merchantProceeds.add(sdde.getMerchantProceeds());
            BigDecimal newplatformBrokerage = platformBrokerage.add(sdde.getPlatformBrokerage());
            BigDecimal newserviceChanrge = serviceChanrge.add(sdde.getServiceCharge());
            BigDecimal newrealityMoney = realityMoney.add(sdde.getRealityMoney());
            BigDecimal newmerchantDiscountAmount = merchantDiscountAmount.add(sdde.getMerchantDiscountAmount());
            BigDecimal newgiftMoney = giftMoney.add(sdde.getGiftMoney());


            cdt.setOrderTotal(neworderTotal);
            cdt.setMerchantProceeds(newmerchantProceeds);
            cdt.setPlatformBrokerage(newplatformBrokerage);
            cdt.setServiceChanrge(newserviceChanrge);
            cdt.setRealityMoney(newrealityMoney);
            cdt.setMerchantDiscountAmount(newmerchantDiscountAmount);
            cdt.setGiftMoney(newgiftMoney);

            int i = baseDao.updateById(cdt);
            c=i;

        }else {
            CtDaysTogetherEntity cdte=new CtDaysTogetherEntity();
            cdte.setPayDate(sdde.getCreateDate());
            cdte.setMerchantId(merchantId);
            if( null!=wxPaymoney ||wxPaymoney.compareTo(num)!=0){
                cdte.setPayType("3");
            }else if( null!=aliPaymoney ||aliPaymoney.compareTo(num)!=0) {
                cdte.setPayType("2");
            }else if( null!=YePaymoney ||YePaymoney.compareTo(num)!=0) {
                cdte.setPayType("1");
            }
            cdte.setOrderTotal(sdde.getOrderTotal());
            cdte.setServiceChanrge(sdde.getServiceCharge());
            cdte.setMerchantProceeds(sdde.getMerchantProceeds());
            cdte.setPlatformBrokerage(sdde.getPlatformBrokerage());
            cdte.setRealityMoney(sdde.getRealityMoney());
            cdte.setMerchantDiscountAmount(sdde.getMerchantDiscountAmount());
            cdte.setGiftMoney(sdde.getGiftMoney());
            int insert = baseDao.insert(cdte);
            c=insert;
        }
        }
        return c;
    }
}
