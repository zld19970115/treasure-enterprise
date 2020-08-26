package io.treasure.enm;

public enum EOrderRewardWithdrawRecord {

    NOT_COPY(1,"未拷"),
    COPYED(2,"已拷-则不能再次拷");

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
