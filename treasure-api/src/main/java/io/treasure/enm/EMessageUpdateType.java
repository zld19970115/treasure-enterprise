package io.treasure.enm;

public enum EMessageUpdateType {


        CREATE_ORDER(1,"新订单"),       //CREATE_ORDER
        ATTACH_ITEM(2,"加菜订单"),       //ATTACH_ORDER
        ATTACH_ROOM(3,"加房订单"),       //ATTACH_ROOM
        REFUND_ORDER(4,"整单退"),        //REFUND_ORDER
        DETACH_ITEM(5,"退菜订单");       //DETACH_ITEM


        private int code;
        private String msg;


    EMessageUpdateType(int code,String msg){
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
