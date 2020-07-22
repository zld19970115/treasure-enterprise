package io.treasure.utils;

import com.google.gson.Gson;
import io.treasure.dto.SharingActivityExtendsDTO;
import io.treasure.entity.SharingActivityExtendsEntity;
import org.junit.Test;

public class ObjectUtil<T> {



    public Object convertObject(Object source,Object resObj){
        Gson gson = new Gson();
        String s = gson.toJson(source);
        Object o = gson.fromJson(s, resObj.getClass());
        System.out.println(o.toString());
        return o;
    }

    public static Object convertEntity(Object source,Class<?> clazz){
        Gson gson = new Gson();
        String s = gson.toJson(source);
        Object o = gson.fromJson(s,clazz);
        System.out.println(o.toString());
        return o;
    }

}
