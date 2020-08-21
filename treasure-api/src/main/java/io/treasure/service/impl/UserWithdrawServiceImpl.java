package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.*;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.entity.*;
import io.treasure.service.ClientUserService;
import io.treasure.service.MerchantWithdrawService;
import io.treasure.service.UserWithdrawService;
import io.treasure.utils.AdressIPUtil;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.PageTotalRowData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Service
public class UserWithdrawServiceImpl   extends CrudServiceImpl<UserWithdrawDao, UserWithdrawEntity, UserWithdrawDTO> implements UserWithdrawService {

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
    @Override
    public QueryWrapper<UserWithdrawEntity> getWrapper(Map<String, Object> params) {
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //审核状态
        String verifyState=(String)params.get("verifyState");
        String userId = (String)params.get("userId");
        QueryWrapper<UserWithdrawEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(verifyState),"verify_state",verifyState);
        wrapper.eq(StringUtils.isNotBlank(userId),"user_id",userId);
        return wrapper;
    }

    @Override
    public List<UserWithdrawDTO> selectByUserIdAndStasus(long UserId) {
        return baseDao.selectByUserIdAndStasus(UserId);
    }

    @Override
    public List<UserWithdrawDTO> selectByUserIdAndalready(long UserId) {
        return baseDao.selectByUserIdAndalready(UserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result audit(UserWithdrawDTO dto, HttpServletRequest request) throws AlipayApiException {
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
            Long userId=dto.getUserId();
            String amount=dto.getMoney().toString();
            String ipAddress= AdressIPUtil.getClientIpAddress(request);
            if(type==1){
                result=this.wxWithdraw(orderNumber,userId,amount,ipAddress,dto);
            }else if(type==2){
                result=this.aliWithdraw(orderNumber,userId,amount,dto);
            }
        } else {

            ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getUserId());
            BigDecimal coin = clientUserEntity.getCoin();
            java.math.BigDecimal bd1 = new java.math.BigDecimal( dto.getMoney());
            coin = coin.add(bd1);
            clientUserEntity.setCoin(coin);
            clientUserService.updateById(clientUserEntity);
            this.update(dto);
        }
        return result;
    }

    @Override
    public PageData<UserWithdrawDTO> listPage(Map<String, Object> params) {
        IPage<UserWithdrawEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        List<UserWithdrawDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), UserWithdrawDTO.class);
    }

    @Override
    public PageTotalRowData<UserWithdrawDTO> getMerchanWithDrawByMerchantId(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<UserWithdrawDTO> page = (Page) baseDao.getMerchanWithDrawByMerchantId(params);
        Map map = new HashMap();
        if(page.getResult() != null && page.getResult().size() > 0) {
            UserWithdrawDTO vo = baseDao.getMerchanWithDrawByMerchantIdTotalRow(params);
            map.put("money",vo.getMoney());
        }
        List<UserWithdrawDTO> list = baseDao.getMerchanWithDrawByMerchantId(params);
        return new PageTotalRowData<>(page.getResult(),page.getTotal(),map);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result wxWithdraw(String orderNumber,Long userId,String amount,String ipAddress,UserWithdrawDTO dto){
        Result result=new Result();
        ClientUserDTO clientUserDTO = clientUserService.get(userId);
        if(clientUserDTO==null){
            throw new RenException("没有此提现用户！");
        }
        String openid=clientUserDTO.getWxAccountOpenid();
        if(openid==null){
            throw new RenException("没有此提现账户！");
        }
//        double cash= merchantDTO.getTotalCash(); //可提现金额
//        BigDecimal bigDecimalCash=BigDecimal.valueOf(cash);
        BigDecimal bigDecimalAmount=new BigDecimal(amount);
//        if(bigDecimalCash.compareTo(bigDecimalAmount)==-1){
//            throw new RenException("可提现金额不足！");
//        }
        BigDecimal coin = clientUserDTO.getCoin();//提现总金额
//        double notCash= merchantDTO.getNotCash(); //未提现金额
//        BigDecimal bigDecimalNotCash=BigDecimal.valueOf(notCash);
        SortedMap<String, String> map = new TreeMap<String, String>();
        if(clientUserDTO.getWxStatus() == 1) {
            map.put("mch_appid","wx6e2e6aa4fa13a6f0");
        } else {
            map.put("mch_appid","wx55a47af8ae69ae28");
        }
        //map.put("mch_appid","wx21ea102be3ebdd99");
        map.put("mchid",wxPayConfig.getMchID());
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
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
//            bigDecimalNotCash=bigDecimalNotCash.subtract(bigDecimalAmount);
//            merchantDTO.setTotalCash(bigDecimalNotCash.doubleValue());
            this.update(dto);
        } else {
            // 提现失败，更新记录
            return result.error("微信提现过程出错,openid:" + clientUserDTO.getWxAccountOpenid() + ",错误代码:" + returnInfo.get("err_code") + ",错误信息:"
                    + returnInfo.get("err_code_des"));
        }
        return result;
    }

    /**
     * 支付宝提现
     * @param orderNumber 本地单号
     * @param amount   金额
     * @param userId 商家编号
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result aliWithdraw(String orderNumber, Long userId, String amount,UserWithdrawDTO dto) throws AlipayApiException {
        Result result=new Result();
        ClientUserDTO clientUserDTO = clientUserService.get(userId);
        if(clientUserDTO==null){
            throw new RenException("没有此提现用户！");
        }
        String account=clientUserDTO.getAliAccountNumber();
        String realName=clientUserDTO.getAliAccountRealname() ;
        if(account==null||realName==null){
            throw new RenException("没有此提现账户！");
        }
//        double cash= merchantDTO.getTotalCash(); //可提现金额
//        BigDecimal bigDecimalCash=BigDecimal.valueOf(cash);
        BigDecimal bigDecimalAmount=new BigDecimal(amount);
//        if(bigDecimalCash.compareTo(bigDecimalAmount)==-1){
//            throw new RenException("可提现金额不足！");
//        }
//        double alreadyCash= merchantDTO.getAlreadyCash(); //已提现金额
//        BigDecimal bigDecimalAlreadyCash=BigDecimal.valueOf(alreadyCash);
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
//            bigDecimalAlreadyCash=bigDecimalAlreadyCash.add(bigDecimalAmount);
//            merchantDTO.setAlreadyCash(bigDecimalAlreadyCash.doubleValue());
//            bigDecimalNotCash=bigDecimalNotCash.subtract(bigDecimalAmount);
//            merchantDTO.setTotalCash(bigDecimalNotCash.doubleValue());
            this.update(dto);
//            merchantService.update(merchantDTO);
        } else { // 支付宝提现失败，本地业务逻辑略
            throw new RenException("调用支付宝提现接口成功,但提现失败!code["+response.getCode()+"],"+response.getMsg()+",["+response.getSubCode()+"]"+response.getSubMsg());
        }
        return result;
    }

//=====================================返现提现接口=====================================================================

    @Transactional(rollbackFor = Exception.class)
    public Result wxMerchantCommissionWithDraw(MerchantSalesRewardRecordEntity entity){
        Result result=new Result();
        Long mId = entity.getMId();
        Integer rewardValue = entity.getRewardValue();
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
                    SendSMSUtil.MerchantsWithdrawal(merchantEntity.getMobile(),entity.getRewardValue()+"", merchantEntity.getName(), smsConfig);
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

        BigDecimal amount=new BigDecimal(entity.getRewardValue());
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
                    SendSMSUtil.MerchantsWithdrawal(merchantEntity.getMobile(),entity.getRewardValue()+"", merchantEntity.getName(), smsConfig);
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
