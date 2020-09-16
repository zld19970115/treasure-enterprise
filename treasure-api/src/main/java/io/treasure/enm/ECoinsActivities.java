package io.treasure.enm;

public class ECoinsActivities {

    public enum Status{
        // status               int default 1     null comment '1有效，2无效',
        ENABLE(1,"有效果"),
        DISABLE(2,"无效");

        private int code;
        private String msg;

        Status(int code, String msg){
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
    public enum Mode{
        //mode  int default 1     null comment '1常规，2忽略人数',
        STANDARD_MODEL(1,"常规,核算预期人数"),
        IGNORE_PERSON_NUM_MODEL(2,"忽略预期人数");

        private int code;
        private String msg;

        Mode(int code, String msg){
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
    public enum NewUserWinAwards{
        //new_user_win_awards  int default 2     null comment '1不区别新老用户，2新用户永远得大奖',
        ALL_USER_IS_THE_SAME(1,"所有用户都一样"),
        FOCRE_NEW_USER_WIN_AWARDS(2,"新用户一定得大奖");

        private int code;
        private String msg;

        NewUserWinAwards(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public static NewUserWinAwards fromCode(Integer isForceNewUser) {
            for(NewUserWinAwards isForce:NewUserWinAwards.values())
                if(isForce.getCode() == isForceNewUser)
                    return isForce;
            return null;
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
    public enum ExpireTimeUnit{

        UNIT_DAYS(1,"天"),
        UNIT_WEEKS(2,"星期"),
        UNIT_MONTHS(3,"月");

        private int code;
        private String msg;

        ExpireTimeUnit(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
        ExpireTimeUnit(int code){
            this.code = code;
        }

        public static ExpireTimeUnit fromCode(Integer expireTimeUnitCode) {
           for(ExpireTimeUnit expireTimeUnit:ExpireTimeUnit.values())
            if(expireTimeUnit.getCode() == expireTimeUnitCode)
                return expireTimeUnit;
            return null;
        }

        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }
    public enum TimeMode{
        EVERY_DAY_OPENING(1,"每天定时开启"),
        ALWAYS_OPEING(2,"一直开启");

        private int code;
        private String msg;

        TimeMode(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public static TimeMode fromCode(Integer timeModeCode) {
            for(TimeMode timeModeEnum:TimeMode.values())
                if(timeModeEnum.getCode() == timeModeCode)
                    return timeModeEnum;
            return null;
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
}
