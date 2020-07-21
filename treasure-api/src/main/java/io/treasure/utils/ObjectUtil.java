package io.treasure.utils;

import com.google.gson.Gson;

public class ObjectUtil<T> {

    private T t;
    public  Object getAnotherObjVimJson(Object o){
        Gson gson = new Gson();
        String obj = gson.toJson(o);
        System.out.println("obj:"+obj);
        gson.fromJson(obj,t.getClass());

        return null;
    }
}
