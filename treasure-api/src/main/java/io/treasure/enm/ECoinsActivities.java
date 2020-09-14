package io.treasure.enm;

public class ECoinsActivities {

    public enum Status{
        // status               int default 1     null comment '1有效，2无效',
        NEW_RECORD(1,"有效果"),
        USED_RECORD(2,"无效");

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
        NEW_RECORD(1,"常规,核算预期人数"),
        USED_RECORD(2,"忽略预期人数");

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
        NEW_USER_WIN_AWARDS(2,"新用户一定得大奖");

        private int code;
        private String msg;

        NewUserWinAwards(int code, String msg){
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
        public int getCode(){
            return this.code;
        }
        public String getMsg(){
            return this.msg;
        }
    }
    public enum TimeMode{
        EVERY_DAY_OPENING(1,"每天定时开启"),
        ALWAYS_OOPEING(2,"一直开启");

        private int code;
        private String msg;

        TimeMode(int code, String msg){
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
}
