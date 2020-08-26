package io.treasure.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.entity.SharingActivityExtendsEntity;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class QueryWrapperUtil<T> {

    @Test
    public void t()throws Exception{
        SharingActivityExtendsEntity sharingActivityExtendsEntity = new SharingActivityExtendsEntity();
        sharingActivityExtendsEntity.setMinimumCharge(200);

        QueryWrapper queryWrapper = new QueryWrapper<SharingActivityExtendsEntity>();

        attachEQConditionFromAnnotation(sharingActivityExtendsEntity,queryWrapper);
    }
    public QueryWrapper<T> attachEQConditionFromAnnotation(Object entity,QueryWrapper<?> queryWrapper) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> aClass = entity.getClass();
        Object qwEntity = aClass.newInstance();
//
//        Field[] fields = aClass.getDeclaredFields();
//        for (Field field : fields) {
//            ObjExtends annotation = field.getAnnotation(ObjExtends.class);
//            if (field.getAnnotation(ObjExtends.class) != null) {
//                String defaultValue = null;//annotation.defaultValue();
//                String queryMethod = null;//annotation.queryMethod();
//                String filedName = field.getName();//字段名
//                String type = field.getGenericType().toString();
//                if(queryMethod.equals("eq")){
//                    String methodName = generGetMethodName(filedName);
//                    Method getMethod = aClass.getDeclaredMethod(methodName);
//                    Object invoke = getMethod.invoke(qwEntity);
//                    System.out.println(qwEntity.toString());
//                    Integer tmp = (Integer)invoke;
//                    if(tmp == null){
//                        Method setMethod = aClass.getDeclaredMethod(filedName);
//                        //setMethod.invoke(qwEntity, Integer.parseInt(defaultValue));
//                        System.out.println(qwEntity.toString());
//
//                    }
//
//                }
//                //queryMethod
//
//              //  Method method = targetClass.getMethod(getName);
//                //Object value = method.invoke(this);
//                //if (value != null) {
//                  //  if (field.getGenericType().toString().contains("Date")) {
//                   //     Date tmpD = (Date) value;
//                    //    res.put(mName, tmpD.getTime() + "");
//                   // } else {
//                  //      res.put(mName, value + "");
//                   // }
//                //}
//            }
//        }
        return null;
    }

    public String generGetMethodName(String fieldName){
        String getName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getName;
    }

    public String generSetMethodName(String fieldName){
        String getName = "Set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getName;
    }
    public String toUnderLineString(String fieldName){
        StringBuilder sb = new StringBuilder();
        //String[] split = field.split("[A-Z]");
        for(int i=0;i<fieldName.length();i++){
            Character c = fieldName.charAt(i);
            if(Character.isUpperCase(c)){
                sb.append("_"+Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

/*
    public Map<String, String> getRedisMap() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Map<String, String> res = new HashMap<>();
        Class<?> targetClass = this.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(RedisMapItem.class) != null) {
                String mName = field.getName();
                String getName = "get" + mName.substring(0, 1).toUpperCase() + mName.substring(1);
                Method method = targetClass.getMethod(getName);
                Object value = method.invoke(this);
                if (value != null) {
                    if (field.getGenericType().toString().contains("Date")) {
                        Date tmpD = (Date) value;
                        res.put(mName, tmpD.getTime() + "");
                    } else {
                        res.put(mName, value + "");
                    }
                }
            }
        }
        return res;
    }


    public Object initParamsFromMap(Map<String, String> map) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> targetClass = this.getClass();

        for (Map.Entry<String, String> m : map.entrySet()) {
            Field field = targetClass.getDeclaredField(m.getKey());
            String type = field.getGenericType().toString();
            String tmp = field.getName();
            String mName = "set" + tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
            Method method = targetClass.getMethod(mName, field.getType());
            if (type.contains("String")) {
                method.invoke(this, m.getValue());
            } else if (type.contains("Integer")) {
                method.invoke(this, Integer.parseInt(m.getValue()));
            } else if (type.contains("Date")) {
                Long l = Long.parseLong(m.getValue());
                method.invoke(this, new Date(l));
            } else if (type.contains("Long")) {
                method.invoke(this, Long.parseLong(m.getValue()));
            }
        }
        return this;

    }

    */
}
