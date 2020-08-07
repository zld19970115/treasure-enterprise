package io.treasure.jra.impl;

import io.treasure.config.MyRedisPool;
import io.treasure.jra.IDishesMenuJRA;
import io.treasure.jro.DishesMenuSet;
import io.treasure.jro.UserSearchSet;
import io.treasure.utils.MyPingyInUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class DishesMenuJRA implements IDishesMenuJRA {

    Jedis jedis = MyRedisPool.getJedis();

    //基本获取字段名称
    public String getFieldName(char letter){
        return DishesMenuSet.fieldPrefix+letter;
    }
    //取得字段的名称
    public Set<String> getSetMembers(char letter){
        return  jedis.smembers(getFieldName(letter));
    }
    //取得当前所有有效集合
    public void getAllList(){

    }

    //删除指定菜名
    public Long delOne(@NotNull String dishesName){
        String s = MyPingyInUtil.toCharPyString(dishesName);
        Long srem = jedis.srem(getFieldName(s.charAt(0)), dishesName);
        return srem;
    }

    //添加菜品名称
    public void add(String dishesName) {
        String s = MyPingyInUtil.toCharPyString(dishesName);

        //jedis.sadd(UserSearchSet.fieldName,userId);
    }

    public boolean isExistMember(String userId,String value) {
        jedis.del(UserSearchSet.compareFiledName);
        jedis.sadd(UserSearchSet.compareFiledName,value);
/*
        Set<String> sinter = jedis.sinter(UserSearchSet.compareFiledName,getFieldName(userId));
        if(sinter.size()>=1)
            return true;
        return false;
  */
return false;
    }
}
