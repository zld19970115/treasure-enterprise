package io.treasure.service.impl;

import io.treasure.enm.EMessageUpdateType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MerchantMessageService {


    Map<String,String> getPendingMessageViaMerchant(String MerchantId, EMessageUpdateType eMessageUpdateType){


        return null;
    }

    //此方法暂时不做
    Map<String,String> getPendingMessageViaMerchant(String MerchantId, String orderId,EMessageUpdateType eMessageUpdateType){
        return null;
    }

}
