package io.treasure.enm;

public enum EWithDrawGroupBy {

    VERIFY_STATE(1,"校验状态-分类汇总"),
    MERCHANT_ID(2,"商户号-分类汇决"),
    TYPE(3,"类型-分类汇总"),
    VERIFY(4,"核验人-分类汇总");

    private int code;
    private String msg;

    EWithDrawGroupBy(int code, String msg){
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
