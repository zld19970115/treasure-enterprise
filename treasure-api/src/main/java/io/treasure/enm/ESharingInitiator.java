package io.treasure.enm;

public enum ESharingInitiator {


    IN_PROCESSING(0),//未完成的活动
    COMPLETE_SUCCESS(1),//活动已完成：成功
    COMPLETE_FAILURE(2);//活动已完成：失败

    private int code;
    private ESharingInitiator(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
