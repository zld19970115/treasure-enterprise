package io.treasure.config;

import lombok.Data;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

/**
 *
 *
 */
@Data
@Service
public class WxMaConfig implements cn.binarywang.wx.miniapp.config.WxMaConfig {

    private String token;

    @Value("${wx.config.miniapp_id}")
    private String appid;

    @Value("${wx.config.miniappsecret}")
    private String secret;

    private String aesKey;

    public String getToken() {
        return this.token;
    }

    @Override
    public String getAccessToken() {
        return null;
    }

    @Override
    public Lock getAccessTokenLock() {
        return null;
    }

    @Override
    public boolean isAccessTokenExpired() {
        return false;
    }

    @Override
    public void expireAccessToken() {

    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {

    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {

    }

    @Override
    public String getJsapiTicket() {
        return null;
    }

    @Override
    public Lock getJsapiTicketLock() {
        return null;
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return false;
    }

    @Override
    public void expireJsapiTicket() {

    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {

    }

    @Override
    public String getCardApiTicket() {
        return null;
    }

    @Override
    public Lock getCardApiTicketLock() {
        return null;
    }

    @Override
    public boolean isCardApiTicketExpired() {
        return false;
    }

    @Override
    public void expireCardApiTicket() {

    }

    @Override
    public void updateCardApiTicket(String apiTicket, int expiresInSeconds) {

    }

    @Override
    public String getAppid() {
        return this.appid;
    }

    public String getSecret() {
        return this.secret;
    }

    public String getAesKey() {
        return this.aesKey;
    }

    @Override
    public String getMsgDataFormat() {
        return null;
    }

    @Override
    public long getExpiresTime() {
        return 0;
    }

    @Override
    public String getHttpProxyHost() {
        return null;
    }

    @Override
    public int getHttpProxyPort() {
        return 0;
    }

    @Override
    public String getHttpProxyUsername() {
        return null;
    }

    @Override
    public String getHttpProxyPassword() {
        return null;
    }

    @Override
    public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
        return null;
    }

    @Override
    public boolean autoRefreshToken() {
        return false;
    }

}