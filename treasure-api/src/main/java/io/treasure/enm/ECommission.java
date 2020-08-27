package io.treasure.enm;

public enum  ECommission {

    DAYS_TYPE(1),       //按间隔日计算
    WEEKS_TYPE(2),      //按星期计算
    MONTHS_TYPE(3);     //按月计算

    private int code;
    private ECommission(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
