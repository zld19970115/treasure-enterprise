package io.treasure.enm;

public enum ESharingInitiator {


    IN_PROCESSING(1),//未完成的活动
    COMPLETE_SUCCESS(2),//活动已完成：成功
    COMPLETE_FAILURE(3);//活动已完成：失败

    private int code;
    private ESharingInitiator(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
