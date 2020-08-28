package io.treasure.enm;

public enum EOrderRewardWithdrawRecord {

    NEW_RECORD(0,"新记录"),
    USED_RECORD(1,"用过-不能再次用");

    private int code;
    private String msg;

    EOrderRewardWithdrawRecord(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
