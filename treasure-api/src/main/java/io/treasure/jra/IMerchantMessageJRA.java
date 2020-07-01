package io.treasure.jra;

import io.treasure.enm.EMessageUpdateType;
import io.treasure.jro.MerchantMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMerchantMessageJRA {


    //检查是否存在
    boolean isMerchantMessageExist(String merchantId);

    //更新
    boolean addRecord(MerchantMessage merchantMessage);

    //更新消息计数器信息
    MerchantMessage updateSpecifyField(String merchantId, EMessageUpdateType eMessageUpdateType);

    //获取当前消息计数器信息
    MerchantMessage getMerchantMessageCounter(String merchantId);

    MerchantMessage initBaseValue(String merchantId);

    List<String> MerchantList();



}
