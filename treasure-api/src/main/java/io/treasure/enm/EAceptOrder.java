package io.treasure.enm;

public enum  EAceptOrder {

    /** 正常**/
    MANUAL_ACEPT_ORDER(0),
    /**禁用**/
    AUTO_ACEPT_ORDER(1);

    private int code;
    private EAceptOrder(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
