package io.treasure.jra.impl;

import io.treasure.config.MyRedisPool;
import io.treasure.jra.IDishesMenuJRA;
import io.treasure.jro.DishesMenuSet;
import io.treasure.jro.UserSearchSet;
import io.treasure.utils.MyPingyInUtil;
import io.treasure.vo.GoodsGroup;
import io.treasure.vo.SimpleDishesVo;
import lombok.ToString;
import org.junit.Test;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DishesMenuJRA implements IDishesMenuJRA {

    Jedis jedis = MyRedisPool.getJedis();

    //基本获取字段名称
    private String getFieldName(char letter){
        return DishesMenuSet.fieldPrefix+letter;
    }
    //取得字段的名称
    private Set<String> getSetMembers(char letter){
        return  jedis.smembers(getFieldName(letter));
    }
    //取得当前所有有效集合
    public List<GoodsGroup> getAllList(){

        List<GoodsGroup> goodsGroups = new ArrayList<>();

        //1--添加字母内容
        for(int i=97;i<123;i++){
            GoodsGroup gg = new GoodsGroup();
            char letter = (char)i;
            gg.setName((letter+"").toUpperCase());
            gg.setList(new ArrayList<SimpleDishesVo>());
            List<SimpleDishesVo> list = gg.getList();
            Set<String> setMembers = getSetMembers(letter);

            for(String s:setMembers){
                list.add(new SimpleDishesVo(s,null,false));
            }
            if(list.size()>0)
                gg.setList(list);
            goodsGroups.add(gg);
        }

        //2--添加井号内容
        GoodsGroup ggn = new GoodsGroup();
        ggn.setName("#");
        ggn.setList(new ArrayList<SimpleDishesVo>());
        List<SimpleDishesVo> listNumber = ggn.getList();
        Set<String> setMembers = getSetMembers('#');
        for(String s:setMembers){
            listNumber.add(new SimpleDishesVo(s,null,false));
        }
        if(listNumber.size()>0)
            ggn.setList(listNumber);
        goodsGroups.add(ggn);

        return goodsGroups;
    }
    //修改菜品名称
    public void update(String oldStr,String newStr){
        delOne(oldStr);
        add(newStr);
    }

    private boolean isNum(String s){
        int codeValue = s.charAt(0);
        if(codeValue < 97)
            return true;
        return false;
    }
    //删除指定菜名
    public Long delOne(@NotNull String dishesName){
        String s = MyPingyInUtil.toCharPyString(dishesName.trim());
        if(isNum(s)){
            Long srem = jedis.srem(getFieldName('#'), dishesName);
            return srem;
        }else{
            Long srem = jedis.srem(getFieldName(s.charAt(0)), dishesName);
            return srem;
        }
    }

    //添加菜品名称
    public void add(String dishesName) {
        String s = MyPingyInUtil.toCharPyString(dishesName.trim());
        if(isNum(s)){
            jedis.sadd(getFieldName('#'), dishesName);
        }else{
            jedis.sadd(getFieldName(s.charAt(0)), dishesName);
        }
    }

    /*
    public boolean isExistMember(String userId,String value) {
        jedis.del(UserSearchSet.compareFiledName);
        jedis.sadd(UserSearchSet.compareFiledName,value);

        Set<String> sinter = jedis.sinter(UserSearchSet.compareFiledName,getFieldName(userId));
        if(sinter.size()>=1)
            return true;
        return false;

        return false;
    }
    */
}
