package io.treasure.config;

import io.treasure.common.sms.SMSConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ISMSConfig extends SMSConfig {
    @Value("${sms.config.accessKeyId}")
    private String accessKeyId;

    @Value("${sms.config.accessSecret}")
    private String accessSecret;
    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getAccessSecret() {
        return accessSecret;
    }
}
