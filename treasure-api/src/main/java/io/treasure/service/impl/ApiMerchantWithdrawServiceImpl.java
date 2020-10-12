
package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;

import io.treasure.common.exception.RenException;

import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.service.ApiMerchantWithdrawService;
import io.treasure.service.MerchantWithdrawService;
import io.treasure.utils.AdressIPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-29
 */
@Service
public class ApiMerchantWithdrawServiceImpl extends CrudServiceImpl<MerchantWithdrawDao, MerchantWithdrawEntity, MerchantWithdrawDTO> implements ApiMerchantWithdrawService {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private IWXConfig wxPayConfig;
    @Autowired
    private IWXPay wxPay;
    @Autowired
    private MerchantWithdrawService merchantWithdrawService;
    @Autowired
    MerchantServiceImpl merchantService;

    @Override
    public QueryWrapper<MerchantWithdrawEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        int status=1;
        QueryWrapper<MerchantWithdrawEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq("status", status);
        return wrapper;
    }

    /**
     * 支付宝提现
     * @param orderNumber 本地单号
     * @param amount   金额
     * @param merchantId 商家编号
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result aliWithdraw(String orderNumber, Long merchantId, String amount,MerchantWithdrawDTO dto) throws AlipayApiException {
        Result result=new Result();
        MerchantDTO merchantDTO=merchantService.get(merchantId);
        if(merchantDTO==null){
            throw new RenException("没有此提现商户！");
        }
        String account=merchantDTO.getAliAccountNumber();
        String realName=merchantDTO.getAliAccountRealname() ;
        if(account==null||realName==null){
            throw new RenException("没有此提现账户！");
        }
//        double cash= merchantDTO.getTotalCash(); //可提现金额
//        BigDecimal bigDecimalCash=BigDecimal.valueOf(cash);
        BigDecimal bigDecimalAmount=new BigDecimal(amount);
//        if(bigDecimalCash.compareTo(bigDecimalAmount)==-1){
//            throw new RenException("可提现金额不足！");
//        }
        double alreadyCash= merchantDTO.getAlreadyCash(); //已提现金额
        BigDecimal bigDecimalAlreadyCash=BigDecimal.valueOf(alreadyCash);
//        double notCash= merchantDTO.getNotCash(); //未提现金额
//        BigDecimal bigDecimalNotCash=BigDecimal.valueOf(notCash);
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2019092667857068","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFEF/Lw33VPYdlkikXgbke3jTvDBJmVDRq51W/+9R9xXZAc9Plm3UsNLhbIyX4XY+ENarfIj9dKBwcTbzQRNK+LUoBhuDc+UF9PRDoXXotP92fDbKuAv1mD1qrcJaeRw5qRRxglkb21PR2cuc34u97kZlNHfurvDM6u6pi3AGzQ+L/+v/cUT1Om9PqtI17M6xK8qME2ZIU9Ld/vVnwsMz/Iv0+rKzBVAiq0Qhy72JzkIIVMlE5ypeHbKn+N1ladqSxo9NB+vYjEGiDXkMLIyTswlYKkZjZ5CrtLm2u2Y0+qO9rf3OL+L7RhpRTO5Qu1ujdqHBl9fxg5650oJzNGmzZAgMBAAECggEABGM61Ww3zP0/ZrEAG99SLFtlYXCDds5WACRqpm72XcNSF+P527tZjMCcR50MFnl3TwO6A6uMbVTyQFir3i42yMCTjSNWbNLnPurxkMfsTGursh/wgV5l3qSo97g5rzRmnEh1HBY4dtEk0ncNesFH5koxxOI1Nz94bPpdLu9UOwX4xxPMQysA80a14mzsr/TACXrxNkW4A6P9TVMhT6sh9SywyjR/0916AgdW1OPY9pesJNAK3Ee+kKmy8rPvU8GQ8uyNMm4tJqflEufMYr4FR8uIRp72gfXk7m4z6WOAmtk+BPDYFLrq+w8jOxVXrL6Fb/CrrMDKXc98R3pwh4NdgQKBgQDP7r6cKEZ7jbD7UF/qxfQfAZkBhAckQVZcSOosqZ/cnrPQj7IhSFwmYeGVw46glf37WPpOtMkwFPzqfyO4kVO62aTEgTM2x/8lzrxz4GHuE9ECqVHXE/5hjLljPf7xjB8Uf9Fc5nxgcCyVB2qxtbeQauvyJItCuSqBBXBJkQl06QKBgQCj0vlq0K2/tN/PWLlvTxSrOwS6l5NBAToGPQ1iuoQC8GjIHsYuFHmd3xsteaIqp1vyH9MeFVdO/sjg+VhVXGY8WtbiVNDM4WZZeWpVcNZMT6pbiSSdDvEsBMAbMU7wtndFonxUxZbwVIsgFZwybQQTzZXNGiZ52b+JP3sxemQCcQKBgQCPE0FXJCNzisi39NM7MaDL4QaOU3Gykb7B224+8yzL2uvx255/ZlH6GynlKl2uw+ayl8QMejtheV+aX4eNzXnmvTGyARDjZfR76Ggl98SK1FniUe29Z8WHDBTYY+VUAc21BkpieTomBam3lhXlWBuKJPhbcqfcbpr/kmV0SXdPWQKBgQCWUhUInRiA6s/eq1PjvdWCVPI+4Kx+nkiGxuiMagaNx9jtn8dLKwB5CuoeLRjPOKfWoLsQRJbLZmAehs017kXlJZk9LoQ1KrHGcfFPGu2YMhVoTovpDXfgYy9/BByiJVuF6tVY18FrHrhWJV4gqwVtwlutSkx5zILCxhwdR2eTEQKBgGXuPtGUrCEzSuetI1TgCw+4DbephyOPs+G6wThaTWQLhUA6D7njzdWl+LSDtIfyyXWgjISSxnvxzKItp0L8ocg6H6dXvfOWZEby/4G7OwhETozo34Ty+gOmZgUkqlwBuLFcopLCJVUuDYoPX2wJ6tOLItZipHr9ZhJFoeJ8d5yI","json","utf-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqSrqWzT7+YJV6wGDNXKOvzZ0xkurGEvTOGv7ih/JWQhqt2j0zygo+YtjEROPD32BpQUJXBhAPIp6Dv2iXMa26EYZi3Q8H3D6qxDvw+aOJ5sYw8KiNil0ylGqLrh+a0TCWdMcR/AjJItrJwWLJsCWpGf4/fAV3xTR8uGY4jCx9ZxggblvtH1jUkFcio2ffn+/+9XLiFT+/2d2xYUYueAVabe4ximzeFsclbPYjYF0O0tWa5e33bGgqhPDakqH4j9gTgb9HS05WgGB769NX+bCP9ekQi849Spo+JLZfj5xgRfMNFvegdKaWtbAILJ90V6r0bfrbvEjLv8+Dejn+15EIwIDAQAB","RSA2");

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
                        "\"out_biz_no\":\""+orderNumber+"\"," +
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
//            bigDecimalCash=bigDecimalCash.subtract(bigDecimalAmount);
//            merchantDTO.setTotalCash(bigDecimalCash.doubleValue());
            bigDecimalAlreadyCash=bigDecimalAlreadyCash.add(bigDecimalAmount);
            merchantDTO.setAlreadyCash(bigDecimalAlreadyCash.doubleValue());
//            bigDecimalNotCash=bigDecimalNotCash.subtract(bigDecimalAmount);
//            merchantDTO.setTotalCash(bigDecimalNotCash.doubleValue());
            this.update(dto);
            merchantService.update(merchantDTO);
        } else { // 支付宝提现失败，本地业务逻辑略
            throw new RenException("调用支付宝提现接口成功,但提现失败!code["+response.getCode()+"],"+response.getMsg()+",["+response.getSubCode()+"]"+response.getSubMsg());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result audit(MerchantWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException {
        Result result=new Result();
        Date date = new Date();
        int state=dto.getVerifyState();
        if(state==1){
            throw new RenException("审核状态不正确！！！");
        }else{
            dto.setVerifyDate(date);
        }
        if(state==2){
            int type=dto.getType();
            String orderNumber=dto.getId().toString();
            Long merchantId=dto.getMerchantId();
            String amount=dto.getMoney().toString();
            String ipAddress= AdressIPUtil.getClientIpAddress(request);
            if(type==1){
                result=this.wxWithdraw(orderNumber,merchantId,amount,ipAddress,dto);
            }else if(type==2){
                result=this.aliWithdraw(orderNumber,merchantId,amount,dto);
            }
        } else {
            this.update(dto);
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

//                //1-1更新返佣
//
//
//                //return new Result().ok("订单翻台成功！");
//                //id为订单id
//                return updateCommissionRecordAndReturn(id);
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

                    //return new Result().ok("订单翻台成功！");
//                    return updateCommissionRecordAndReturn(id);
                }
            }
            BigDecimal totalCash = bigDecimal.add(bigDecimal1);
            merchantEntity.setTotalCash(totalCash.doubleValue());
            merchantEntity.setAlreadyCash(0.00);
            merchantEntity.setNotCash(bigDecimal.doubleValue());
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcashZore);
            merchantService.updateById(merchantEntity);


            //return new Result().ok("订单翻台成功！");
//            return updateCommissionRecordAndReturn(id);
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

//            return updateCommissionRecordAndReturn(id);
            //return new Result().ok("订单翻台成功！");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result wxWithdraw(String orderNumber,Long merchantId,String amount,String ipAddress,MerchantWithdrawDTO dto){
        Result result=new Result();
        MerchantDTO merchantDTO=merchantService.get(merchantId);
        if(merchantDTO==null){
            throw new RenException("没有此提现商户！");
        }
        String openid=merchantDTO.getWxAccountOpenid();
        String realName=merchantDTO.getWxAccountRealname();
        if(openid==null||realName==null){
            throw new RenException("没有此提现账户！");
        }
//        double cash= merchantDTO.getTotalCash(); //可提现金额
//        BigDecimal bigDecimalCash=BigDecimal.valueOf(cash);
        BigDecimal bigDecimalAmount=new BigDecimal(amount);
//        if(bigDecimalCash.compareTo(bigDecimalAmount)==-1){
//            throw new RenException("可提现金额不足！");
//        }
        double alreadyCash= merchantDTO.getAlreadyCash(); //提现总金额
        BigDecimal bigDecimalAlreadyCash=BigDecimal.valueOf(alreadyCash);
//        double notCash= merchantDTO.getNotCash(); //未提现金额
//        BigDecimal bigDecimalNotCash=BigDecimal.valueOf(notCash);
        SortedMap<String, String> map = new TreeMap<String, String>();
        if(merchantDTO.getWxStatus() == 1) {
            map.put("mch_appid","wx37dffd395a91d089");
        } else {
            map.put("mch_appid","wx21ea102be3ebdd99");
        }
        //map.put("mch_appid","wx21ea102be3ebdd99");
        map.put("mchid",wxPayConfig.getMchID());
        map.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        map.put("partner_trade_no",orderNumber);//本地订单编号
        map.put("openid",openid);//openid

        map.put("check_name","NO_CHECK");
        //map.put("re_user_name",realName);//收款人真实姓名
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");

        BigDecimal newAmount = bigDecimalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        map.put("amount",df.format(newAmount));//金额
        map.put("desc","聚宝订餐平台提现!");
        map.put("spbill_create_ip",ipAddress);//IP

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
            // 提现成功，修改记录
//            bigDecimalCash=bigDecimalCash.subtract(bigDecimalAmount);
//            merchantDTO.setTotalCash(bigDecimalCash.doubleValue());
            bigDecimalAlreadyCash=bigDecimalAlreadyCash.add(bigDecimalAmount);
            merchantDTO.setAlreadyCash(bigDecimalAlreadyCash.doubleValue());
//            bigDecimalNotCash=bigDecimalNotCash.subtract(bigDecimalAmount);
//            merchantDTO.setTotalCash(bigDecimalNotCash.doubleValue());
            this.update(dto);
            merchantService.update(merchantDTO);

        } else {
            // 提现失败，更新记录
            return result.error("微信提现过程出错,openid:" + merchantDTO.getWxAccountOpenid() + ",错误代码:" + returnInfo.get("err_code") + ",错误信息:"
                    + returnInfo.get("err_code_des"));
        }
        return result;
    }

}