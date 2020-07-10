package io.treasure.jra.impl;

import io.treasure.config.MyRedisPool;
import io.treasure.jra.IMerchantSetJRA;
import io.treasure.jro.MerchantSet;
import org.junit.Test;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class MerchantSetJRA implements IMerchantSetJRA {


    Jedis jedis = MyRedisPool.getJedis();


    @Override
    public void add(String merchantId) {
        jedis.sadd(MerchantSet.fieldName,merchantId);
    }

    @Override
    public void removeAll() {
        jedis.del(MerchantSet.fieldName);
    }

    public void delItem(String value){
        jedis.srem("name",value);
    }
    @Override
    public boolean isExistMember(String merchantId) {
        jedis.del(MerchantSet.compareFiledName);
        jedis.sadd(MerchantSet.compareFiledName,merchantId);

        Set<String> sinter = jedis.sinter(MerchantSet.compareFiledName, MerchantSet.fieldName);
        if(sinter.size()>=1)
            return true;
        return false;
    }

}
