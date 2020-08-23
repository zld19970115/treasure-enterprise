package io.treasure.enm;

public enum EOrderRewardWithdrawRecord {

    NOT_WITHDRAW(1,"未提现"),
    WITHDRAW_COMPLETE(2,"提现完成");

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
