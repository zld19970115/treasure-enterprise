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
        Integer rewardValue = 0;//entity.getRewardValue();
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
            map.put("mch_appid","wx6e2e6aa4fa13a6f0");
        } else {
            map.put("mch_appid","wx55a47af8ae69ae28");
        }
        map.put("mchid",wxPayConfig.getMchID());
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        map.put("partner_trade_no",mId+"");
        map.put("openid",openid);
        map.put("check_name","NO_CHECK");
        //map.put("re_user_name",realName);//收款人真实姓名
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");

        //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        String fen = new BigDecimal(rewardValue+"").multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        map.put("amount",df.format(fen));//金额

        map.put("desc","mch_commission!");//描述
        if(ipAddress != null){
            map.put("spbill_create_ip",ipAddress);//IP
        }
        String orderInfo = null;
        Map<String, String> returnInfo=new HashMap<String, String>();
        try {
            map.put("sign",WXPayUtil.generateSignature(map,wxPayConfig.getKey()));
            orderInfo = WXPayUtil.mapToXml(map);
            //生成交易记录,这一步才调用微信提现接口，上面的是封装参数
            String returnXml=wxPay.requestWithCert("/mmpaymkttransfers/promotion/transfers",map,wxPayConfig.getHttpConnectTimeoutMs(),wxPayConfig.getHttpReadTimeoutMs());
            returnInfo=  WXPayUtil.xmlToMap(returnXml);
        } catch (Exception e) {
            throw new RenException(e.getMessage());
        }
        if ("SUCCESS".equals(returnInfo.get("return_code"))
                && "SUCCESS".equals(returnInfo.get("result_code"))) {
            //更新提现记录
            entity.setCashOutStatus(2);
            merchantSalesRewardRecordDao.updateById(entity);
            MerchantSalesRewardRecordEntity entity1 = merchantSalesRewardRecordDao.selectById(entity.getId());

            if(entity1 != null){
                if(entity1.getCashOutStatus() == 2){
                    //发送成功消息
                     SendSMSUtil.MerchantsWithdrawal(merchantEntity.getMobile(),entity.getCommissionVolume()+"", merchantEntity.getName(), smsConfig);
                    return result;
                }
            }
            System.out.println("更新失败"+ TimeUtil.simpleDateFormat.format(new Date())+":"+entity.getId());

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
    public Result AliMerchantCommissionWithDraw(MerchantSalesRewardRecordEntity entity) throws AlipayApiException {

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

        BigDecimal amount=new BigDecimal("0");//entity.getRewardValue());
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
        //构造client
        //构造API请求
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
//        Map<String, String> map = new LinkedHashMap<String, String>();
//        map.put("out_biz_no", orderNumber);
//        map.put("payee_type", "ALIPAY_LOGONID");
//        map.put("payee_account",account);
//        map.put("amount", amount);
//        map.put("payer_show_name", "聚宝订餐平台用户提现");
//        map.put("payee_real_name",realName);
//        map.put("remark", "您的提现已转出请查收。");
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

            //更新提现记录
            entity.setCashOutStatus(2);
            merchantSalesRewardRecordDao.updateById(entity);
            MerchantSalesRewardRecordEntity entity1 = merchantSalesRewardRecordDao.selectById(entity.getId());

            if(entity1 != null){
                if(entity1.getCashOutStatus() == 2){
                    //发送成功消息
                     SendSMSUtil.MerchantsWithdrawal(merchantEntity.getMobile(),entity.getCommissionVolume()+"", merchantEntity.getName(), smsConfig);
                    return result;
                }
            }
            System.out.println("更新失败"+ TimeUtil.simpleDateFormat.format(new Date())+":"+entity.getId());

        } else { // 支付宝提现失败，本地业务逻辑略
            throw new RenException("调用支付宝提现接口成功,但提现失败!code["+response.getCode()+"],"+response.getMsg()+",["+response.getSubCode()+"]"+response.getSubMsg());
        }
        return result;
    }
}
