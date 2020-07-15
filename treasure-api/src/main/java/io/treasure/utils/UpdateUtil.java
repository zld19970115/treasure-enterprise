package io.treasure.utils;

import java.lang.reflect.Field;

public class UpdateUtil {
    private String idFieldName;

    public UpdateUtil(){

    }

    public UpdateUtil(String fullClassName,String idFieldName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.idFieldName = idFieldName;
        Class<?> aClass = Class.forName(fullClassName);

        Object o = aClass.newInstance();

        Field[] declaredFields = aClass.getDeclaredFields();
        for(int i=0;i<declaredFields.length;i++){
            Field declaredField = declaredFields[i];

            //指定排除的字段
            if(declaredField.equals(idFieldName)){
                System.out.println("字段名称=id==="+declaredField.toString());
            }else{
                //其它字段
                System.out.println("字段名称===="+declaredField.toString());

            }
        }
    }

}
