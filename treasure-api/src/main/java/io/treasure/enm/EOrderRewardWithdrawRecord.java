package io.treasure.enm;

public class EOrderRewardWithdrawRecord {

    public enum ERecordStatus{
        NEW_RECORD(0,"新记录"),
        USED_RECORD(1,"用过-不能再次用");

        private int code;
        private String msg;

        ERecordStatus(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
        public ERecordStatus fromCode(int code){
            for (ERecordStatus eRecordStatus:ERecordStatus.values()){
                if(eRecordStatus.getCode() == code)
                    return eRecordStatus;
            }
            return null;
        }
    }

    public enum TimeMode{

        UNIT_DAYS(1,"天"),
        UNIT_WEEKS(2,"星期"),
        UNIT_MONTHS(3,"月");

        private int code;
        private String msg;

        TimeMode(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
        TimeMode(int code){
            this.code = code;
        }

        public static TimeMode fromCode(Integer code) {
            for(TimeMode timeMode:TimeMode.values())
                if(timeMode.getCode() == code)
                    return timeMode;
            return null;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }

    public enum EJudgeMethod{

        SALES_VOLUME_MODE(1,"销量"),
        TRADE_NUM_MODE(2,"名次"),
        FIXED_VALUE_MODE(3,"固定值");

        private int code;
        private String msg;

        EJudgeMethod(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
        EJudgeMethod(int code){
            this.code = code;
        }

        public static EJudgeMethod fromCode(Integer code) {
            for(EJudgeMethod jMethod:EJudgeMethod.values())
                if(jMethod.getCode() == code)
                    return jMethod;
            return null;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }

}
