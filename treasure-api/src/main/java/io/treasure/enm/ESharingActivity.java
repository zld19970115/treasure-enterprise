package io.treasure.enm;

import lombok.Data;

public class ESharingActivity {


    public enum MemberHelper{
        NEW_USER_ONLY(1),       //仅新用户有效
        ALL_USER(2);            //所有用户及新用户均有效

        private int code;

        MemberHelper(int code){
            this.code = code;
        }
        public int getCode(){
            return this.code;
        }

    }

    public enum SharingMethod{
        PEOPLE_NUM_MODE(1),     //人数模式
        UNDEFINE(2);            //暂时未定义

        private int code;

        SharingMethod(int code){
            this.code = code;
        }
        public int getCode(){
            return this.code;
        }

    }

    public enum RewardType{

        FREE_GOLD_TYPE(1),      //代付金方式
        GOODS_TYPE(2),          //商品方式
        DISHES_TYPE(3);         //菜品方式

        private int code;
        RewardType(int code){
            this.code = code;
        }
        public int getCode(){
            return this.code;
        }
    }

    public enum InStoreOnly{
        SPECIFY_ENABLE(1),      //仅店内有效
        OVERALL_ENABLE(2);      //所有区域均有效

        private int code;
        InStoreOnly(int code){
            this.code = code;
        }
        public int getCode(){
            return this.code;
        }
    }
}
