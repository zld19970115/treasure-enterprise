package io.treasure.enm;

public class  ESharingRewardGoods {

    public enum Status{
        REWARD_ENABLE(1,"未使用"),       //CREATE_ORDER
        REWARD_USED(2,"已使用"),       //ATTACH_ORDER
        REWARD_DISABLED(3,"强制失效");       //ATTACH_ROOM

        private int code;
        private String msg;

        Status(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }
    public enum OrderStatus{
        REQUIRE_GIFT_ORDER(1,"预计"),             //
        ACEPT_GIFT_ORDER(2,"接授"),               //
        SALE_OUT_TODAY(3,"今日售完"),             //
        CHANGE_OTHER_GOODS(4,"改同价商品");       //

        private int code;
        private String msg;

        OrderStatus(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }

    public enum GoodsType{

        DISHES_TYPE(5,"菜品"),             //
        GOODS_TYPE(6,"商品");               //

        private int code;
        private String msg;

        GoodsType(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }

    public enum ActityValidityUnit{
        UNIT_DAYS(1,"天"),             //
        UNIT_WEEKS(2,"星期"),               //
        UNIT_MONTHS(3,"月");

        private int code;
        private String msg;

        public static ActityValidityUnit fromCode(Integer expireTimeUnitCode) {
            for(ActityValidityUnit tmpUnit:ActityValidityUnit.values())
                if(tmpUnit.getCode() == expireTimeUnitCode)
                    return tmpUnit;
            return null;
        }
        ActityValidityUnit(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }
}
