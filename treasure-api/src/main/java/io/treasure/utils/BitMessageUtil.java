package io.treasure.utils;

import org.junit.Test;

public class BitMessageUtil {

    private int messageCode;



    private int convertCode(EMsgCode eMsgCode){
        return 1<<eMsgCode.ordinal();
    }
    public int attachMessage(EMsgCode eMsgCode){
       return messageCode = messageCode|convertCode(eMsgCode);
    }
    public boolean targetBitStatus(int target,int bitPos){
        if((target &(1<<bitPos)) == 0)
            return false;
        return true;
    }

    public int deatchMsg(EMsgCode eMsgCode){
        return messageCode = messageCode &(~convertCode(eMsgCode));
    }




}
