package io.treasure.enm;

public enum EClientUserVersion {
    PLACE_ORDER_PROCESS(1),//下单
    REFUND_ORDER_PROCESS(2);//退单

    private int code;
    private EClientUserVersion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
