package io.treasure.config;

import io.treasure.common.constant.WXPayConstants;
import io.treasure.common.wx.IWXPayDomain;
import io.treasure.common.wx.WXConfig;
import io.treasure.utils.PathUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 微信支付配置封装
 * @author super
 * @version 1.0
 * @since 2019.3.29
 */
@Service
public class IWXConfig extends WXConfig {

    private byte[] certData;

    @Value("${wx.config.app_id}")
    private String app_id;

    @Value("${wx.config.appsecret}")
    private String app_secret;

    @Value("${wx.pay.key}")
    private String wx_pay_key;

    @Value("${wx.pay.mch_id}")
    private String wx_pay_mch_id;

    @Value("${wx.pay.notifyUrl}")
    private String notifyUrl;

    public IWXConfig() throws Exception {

        String os = System.getProperty("os.name");
        String certPath = "/www/apiclient_cert.p12";
        if(os.toLowerCase().startsWith("win")){
            certPath = "C:\\apiclient_cert.p12";
        }
        //String certPath = "C:\\apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    public String getAppID() {
        return app_id;
    }

    @Override
    public String getAppSecret() {
        return app_secret;
    }

    @Override
    public String getMchID() {
        return wx_pay_mch_id;
    }

    @Override
    public String getKey() {
        return wx_pay_key;
    }

    @Override
    public String getNotifyUrl() {
        return notifyUrl;
    }

    @Override
    public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        AtomicReference<IWXPayDomain> iwxPayDomain = new AtomicReference<>(new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            @Override
            public DomainInfo getDomain(WXConfig config) {
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        });
        return iwxPayDomain.get();
    }
}
