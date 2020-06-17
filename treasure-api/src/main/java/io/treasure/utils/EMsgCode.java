package io.treasure.utils;

public enum EMsgCode {

    NEW_ORDER(0),
    ADD_DISHES(1),
    REFUND_ORDER(2);

    private int code;
    private EMsgCode(int code){
        this.code = code;
    }
}
