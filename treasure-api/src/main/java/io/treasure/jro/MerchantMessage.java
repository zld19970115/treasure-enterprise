package io.treasure.jro;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;


@Data
@ToString
public class MerchantMessage {


    public static final String MCH_MSG_PREFIX = "mm:";
    private String fieldName;
    private String merchantId;

    private int createOrderCounter =0;
    private int attachItemCounter =0;
    private int attachRoomCounter =0;
    private int refundOrderCounter =0;
    private int detachItemCounter =0;


    public static String getCreateOrderCounterField(){
        return "coc";
    }

    public static String getAttachItemCounterField() {
        return "aic";
    }

    public static String getAttachRoomCounterField() {
        return "arc";
    }

    public static String getRefundOrderCounterField() {
        return "roc";
    }

    public static String getDetachItemCounterField() {
        return "dic";
    }

    public String initFieldName() {
        return fieldName =  MCH_MSG_PREFIX + merchantId;
    }
    public MerchantMessage initFieldName(String merchantId) {
        fieldName = MCH_MSG_PREFIX + merchantId;
        return this;
    }

}
