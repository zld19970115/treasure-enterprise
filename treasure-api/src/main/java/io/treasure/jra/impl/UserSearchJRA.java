package io.treasure.jra.impl;

import io.treasure.config.MyRedisPool;
import io.treasure.jra.IUserSearchJRA;
import io.treasure.jro.MerchantSet;
import io.treasure.jro.UserSearchSet;
import org.junit.Test;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

@Component
public class UserSearchJRA implements IUserSearchJRA {
    Jedis jedis = MyRedisPool.getJedis();

    @Test
    public void test(){

    }
    public String getFieldName(String userId){
        return UserSearchSet.fieldName+userId;
    }

    public Set<String> getSetMembers(String userId){
        return  jedis.smembers(getFieldName(userId));
    }

    public String delOne(String userId){
        Set<String> smembers = jedis.smembers(getFieldName(userId));
        for (String smember : smembers) {
            delItem(userId,smember);
            return  smember;
        }
        return null ;
    }
    @Override
    public void add(String userId,String value) {
        int length = value.length()-1;
        //List<String> items = new ArrayList<>();
        for(int i =1 ; i<length;i++){
            //   items.add(value.substring(0,i));
            String tmp = value.substring(0,i);
            if(isExistMember(userId,tmp))
                delItem(userId,tmp);
        }
        Long scard = jedis.scard(getFieldName(userId));
        if (scard>21){
            delOne(userId);
        }
        jedis.sadd(getFieldName(userId),value);
    }

//    @Override
//    public void add(String userId) {
//        jedis.sadd(UserSearchSet.fieldName,userId);
//    }

    @Override
    public Long removeAll(String userId) {
        Long del = jedis.del(getFieldName(userId));
        return del;
    }
    @Override
    public Long delItem(String userId,String value){
        Long srem = jedis.srem(getFieldName(userId), value);
        return srem;
    }
    @Override
    public boolean isExistMember(String userId,String value) {
        jedis.del(UserSearchSet.compareFiledName);
        jedis.sadd(UserSearchSet.compareFiledName,value);

        Set<String> sinter = jedis.sinter(UserSearchSet.compareFiledName,getFieldName(userId));
        if(sinter.size()>=1)
            return true;
        return false;
    }

}
