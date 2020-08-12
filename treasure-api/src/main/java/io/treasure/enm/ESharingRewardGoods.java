package io.treasure.enm;

public enum  ESharingRewardGoods {

        REWARD_ENABLE(1,"未使用"),       //CREATE_ORDER
        REWARD_USED(2,"已使用"),       //ATTACH_ORDER
        REWARD_DISABLED(3,"失效");       //ATTACH_ROOM

        private int code;
        private String msg;


    ESharingRewardGoods(int code,String msg){
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
