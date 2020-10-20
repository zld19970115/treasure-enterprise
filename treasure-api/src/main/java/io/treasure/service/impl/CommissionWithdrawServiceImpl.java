package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import io.treasure.common.exception.RenException;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.MerchantDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.CommissionWithdrawService;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CommissionWithdrawServiceImpl implements CommissionWithdrawService {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private IWXConfig wxPayConfig;
    @Autowired
    private IWXPay wxPay;

    @Autowired(required = false)
    private MerchantDao merchantDao;

    @Autowired
    private SMSConfig smsConfig;

    @Autowired
    private MerchantSalesRewardServiceImpl merchantSalesRewardService ;
    @Autowired(required = false)
    private MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;

    @Autowired
    ClientUserService clientUserService;

    @Transactional(rollbackFor = Exception.class)
    public Result wxMerchantCommissionWithDraw(MerchantSalesRewardRecordEntity entity){
        Result result=new Result();
        Long mId = entity.getMId();
        BigDecimal commissionVolume = entity.getCommissionVolume();
        MerchantEntity merchantEntity = merchantDao.selectById(mId);
        String ipAddress = merchantEntity.getMchIp();
        if(merchantEntity==null){
            return new Result().error("id不存在，未找到此商户和户！");
        }
        String openid=merchantEntity.getWxAccountOpenid();
        if(openid==null){
            return new Result().error("佣金提现：请先绑定微信提现参数(openid)！");
        }

        SortedMap<String, String> map = new TreeMap<String, String>();
        if(merchantEntity.getWxStatus() == 1) {
           map.put("mch_appid","wx37dffd395a91d089");//申请商户号的appid或商户号绑定的appid
        } else {
            map.put("mch_appid","wx21ea102be3ebdd99");//申请商户号的appid或商户号绑定的appid
        }
        map.put("mchid",wxPayConfig.getMchID());//微信支付分配的商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        map.put("partner_trade_no",entity.getId()+"");//商户订单号，需保持唯一性(只能是字母或者数字，不能包含有其它字符)
        map.put("openid",openid);//商户appid下，某用户的openid
        map.put("check_name","NO_CHECK");
        String fen = commissionVolume.multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        map.put("amount",fen.trim());//金额
        map.put("desc","mch_commission!");//描述
//        if(ipAddress != null){
//            map.put("spbill_create_ip",ipAddress);//IP
//        }
        String orderInfo = null;
        Map<String, String> returnInfo=new HashMap<String, String>();
        try {
            map.put("sign",WXPayUtil.generateSignature(map,wxPayConfig.getKey()));
            orderInfo = WXPayUtil.mapToXml(map);
            //生成交易记录,这一步才调用微信提现接口，上面的是封装参数
            String returnXml=wxPay.requestWithCert("/mmpaymkttransfers/promotion/transfers",map,wxPayConfig.getHttpConnectTimeoutMs(),wxPayConfig.getHttpReadTimeoutMs());
            returnInfo=  WXPayUtil.xmlToMap(returnXml);
            System.out.println(returnInfo.toString());
        } catch (Exception e) {
            System.out.println("返现签名异常");
            throw new RenException(e.getMessage());
        }
        if ("SUCCESS".equals(returnInfo.get("return_code"))
                && "SUCCESS".equals(returnInfo.get("result_code"))) {
            //提现成功
            updateAndReturn(entity,merchantEntity,commissionVolume);//提现成功

        } else {
            // 提现失败，更新记录
            return result.error("微信提现过程出错,openid:" + merchantEntity.getWxAccountOpenid() + ",错误代码:" + returnInfo.get("err_code") + ",错误信息:"
                    + returnInfo.get("err_code_des"));
        }
        return result;
    }

    /**
     * 支付宝提现
     */
    @Transactional(rollbackFor = Exception.class)
    public Result aliMerchantCommissionWithDraw(MerchantSalesRewardRecordEntity entity) throws AlipayApiException {

        Result result=new Result();
        Long mId = entity.getMId();
        MerchantEntity merchantEntity = merchantDao.selectById(mId);
        if(merchantEntity==null){
            return new Result().error("id不存在，未找到此商户和户！");
        }
        String account=merchantEntity.getAliAccountNumber();
        String realName=merchantEntity.getAliAccountRealname() ;
        if(account==null||realName==null){
            throw new RenException("佣金提现：请提前绑定支付宝相关帐号！");
        }

        BigDecimal amount= entity.getCommissionVolume();
        //构造client
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        //设置网关地址
        certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
        //设置应用Id
        certAlipayRequest.setAppId("2019111969253551");
        //设置应用私钥
        certAlipayRequest.setPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAQMzP/LT1DsnEaUqKofCp12k10h4CgyO8M7i97vn6C9eKvIn9b4OGBV7U3xHxgYusnc3HkY94i3qOMKtR8C2018UzCIo+BJXGGJ1jRs9KGDIA4uFsIfkdyigDgplul84EiRdG0L0ZIj8kCsosA82b8fj/vSO2YRfltwXq4c15yAbNOleWZqvP94UybJ5Lct2R2x8dBlNaTsRjEk1NaRfuboLeFzSfclKBSG/vXkQvzqckBK/xLeLka+7vsRVzbJhgqTqHrALYkuaR5UolLMAcJftYcfR3fIAwSIsZsNYrORufEfxqZqOJauTfeJroR2P+gVsK6g9GJI3P3iSgoji3AgMBAAECggEAS44r7+GEzHpPWV136hvSlS0PMBOvr5USSjiZdiuhGl/lCaUnJe7ZZaZeqpIwXxVmayRpZvERzXIjbBY8fitCCzxxR6kni1AJ/JxX3lhJxvjTTf3cUb7YRJjaOObuncQTNz0ZZL9MUcBfyC5lXf7wo0TpcrhGfLqTxbe0H6c2NZFzaWuwUSOs3q0WwrY0gY/wMk0PlzF1tzD62hCKKz2Y7ZW6Kq7/E2m44ARqRdoIWa5wSBudBOKAFop/+ZfgJI4bRc61MVXPMSKP3zvCnGa+rVHoVbKF5TfErogeSBvuYdq1T+qV7xMe85vGQvg15dyicVDhKy9X4xakD6JH/dM2wQKBgQD5W70HMHAqmk5eCq43aomXP6FY5JZBXHypGKkZkA0oX+brB2kbVUFO2DLALp0p9DNM8Nhw6FNH2Cu3PzLTW+0w+Wlgy+01bKl1s9I0sybs5WiwxjzJTPoMsKHbrTYTz7O8zX4HQktRIawIH/My1hKGdTxKslFmLSIdcyBB+1DGjwKBgQDFX6/eXWpCXnjzyk2yTSCZl2RBqmmpBk1V0tNkhkwH77X+K8ADFL/IC96tyEP2iIKkrjla5llX91oxr2GntVBAwnlFRxD59e7q3bhq9nE4Rv6Yx0us8BbEs2hgdm/NEwgkTxgYDCWE/V3VuTb+5C6ASPLR9y9P0ULvNOGXbFA/WQKBgDfc33ou1dIVg5z505HColRqAuGBRFAcQik3xxpc9TWoVnkszdU7wkfBk40OZFMzzBJemn6g7ZdYzGJfHCnRnE+ucHco+FlRoJ0nzd3UjNHhixSfNJr8TcBuCbTHFyhVDbUsbCGALpNcccfYMImg/8FznjA1xpqXXd8vHXjqrmvtAoGBAMJ93Au++aqU9ZMmUxHUNr+jE0Qx7RSGBUcBDRYN0HYxnKDt87QVijZSnAebRH13X2Vv2UzdmES6lcJIFG7ymDZ4bI/7y5rE4b3G5qdgWYkfTFq4aLXtkEIcmEoV622lx2wgFJn9visikIi+jpb1u2zmdYC5l4GLr+2Pqo1QSnNhAoGBANgz4EhZFg+98+zocwKlr8xywSiIC5DsKZlwKl14Sz5y38Rgd5/Qgfa664rsD1R+ZsLxTiEpPyHx/F0DQTp/DW+X+mapWVXRLww+1k3sgm7wsos2so9rQrRKufG+6xa6SDKFyQygDBT9TnUOOHQpzxHxmFZ2UfSFlGV8cbsJUu7x");
        //设置请求格式，固定值json
        certAlipayRequest.setFormat("json");
        //设置字符集
        certAlipayRequest.setCharset("utf-8");
        //设置签名类型
        certAlipayRequest.setSignType("RSA2");
        //设置应用公钥证书路径
        String os = System.getProperty("os.name");
        String app_cert_path = "/www/appCertPublicKey_2019111969253551.crt";
        if(os.toLowerCase().startsWith("win")){
            app_cert_path = "C:\\appCertPublicKey_2019111969253551.crt";
        }
        certAlipayRequest.setCertPath(app_cert_path);
        //设置支付宝公钥证书路径
        String alipay_cert_path = "/www/alipayCertPublicKey_RSA2.crt";
        if(os.toLowerCase().startsWith("win")){
            alipay_cert_path = "C:\\alipayCertPublicKey_RSA2.crt";
        }
        certAlipayRequest.setAlipayPublicCertPath(alipay_cert_path);
        //设置支付宝根证书路径
        String alipay_root_cert_path = "/www/alipayRootCert.crt";
        if(os.toLowerCase().startsWith("win")){
            alipay_root_cert_path = "C:\\alipayRootCert.crt";
        }
        certAlipayRequest.setRootCertPath(alipay_root_cert_path);
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        request.setBizContent(
                "{" +
                        "\"out_biz_no\":\""+mId+"\"," +
                        "\"trans_amount\":"+amount+"," +
                        "\"product_code\":\"TRANS_ACCOUNT_NO_PWD\"," +
                        "\"biz_scene\":\"DIRECT_TRANSFER\"," +
                        "\"order_title\":\"聚宝订餐平台用户提现\"," +
                        "\"payee_info\":{" +
                        "\"identity\":\""+account+"\"," +
                        "\"identity_type\":\"ALIPAY_LOGON_ID\"," +
                        "\"name\":\""+realName+"\"," +
                        "    }," +
                        "\"remark\":\"您的提现已转出请查收\"," +
                        "\"business_params\":\"{\\\"payer_show_name\\\":\\\"聚宝订餐\\\"}\"," +
                        "  }"
        );
        AlipayFundTransUniTransferResponse response = null;
        try {
            response = alipayClient.certificateExecute(request);
        } catch (AlipayApiException e) {
            throw new RenException(e.getErrMsg());
        }
        if ("10000".equals(response.getCode())) { //提现成功,本地业务逻辑略

            updateAndReturn(entity,merchantEntity,amount);//提现成功

        } else { // 支付宝提现失败，本地业务逻辑略
            throw new RenException("调用支付宝提现接口成功,但提现失败!code["+response.getCode()+"],"+response.getMsg()+",["+response.getSubCode()+"]"+response.getSubMsg());
        }
        return result;
    }

    public Result updateAndReturn(MerchantSalesRewardRecordEntity entity,MerchantEntity merchantEntity,BigDecimal amount){
        //更新奖励记录
        //entity.setCashOutStatus(2);
        //merchantSalesRewardRecordDao.updateById(entity);
        merchantSalesRewardRecordDao.insertEntity(entity);//插入新记录

//        //更新商户提现等参数金额
//        merchantEntity.setCommissionNotWithdraw(merchantEntity.getCommissionNotWithdraw().subtract(amount));
//        merchantEntity.setCommissionAudit(merchantEntity.getCommissionAudit().subtract(amount));
//        merchantEntity.setCommissionWithdraw(merchantEntity.getCommissionWithdraw().add(amount));
//        merchantDao.updateById(merchantEntity);

        //发送成功消息
        SendSMSUtil.MerchantsWithdrawal(merchantEntity.getMobile(),entity.getCommissionVolume()+"", merchantEntity.getName(), smsConfig);

        //检查更新状态，异常则需要手动更改
        MerchantSalesRewardRecordEntity updatedEntity = merchantSalesRewardRecordDao.selectById(entity.getId());
        if(updatedEntity != null){
            if(updatedEntity.getCashOutStatus() != 2){
                System.out.println("提现成功，记录更新失败"+ TimeUtil.simpleDateFormat.format(new Date())+":"+entity.toString());
            }
        }
        return new Result().ok("success");
    }
}
