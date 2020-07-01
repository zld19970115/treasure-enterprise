package io.treasure.jra;

import org.springframework.stereotype.Component;

/*
    仅用于识别消息更新情况
 */

public interface IMerchantSetJRA {

    void add(String merchantId);

    void removeAll();

    boolean isExistMember(String merchantId);

}
