package io.treasure.enm;

public class EApiV3Enm {
    public enum HttpStatusCode{
        OK(200,"处理成功"),
        NO_CONTENT(204,"处理成功，无返回Body"),
        BAD_REQUEST(400,"协议或者参数非法"),
        UNAUTHORIZED(401,"签名验证失败"),
        FORBIDDEN(403,"权限异常"),
        NOT_FOUND(404,"请求的资源不存在"),
        TOO_MANY_REQUESTS(429,"请求超过频率限制"),
        SERVER_ERROR(500,"系统错误"),
        BAD_GATEWAY(502,"服务下线，暂时不可用"),
        SERVICE_UNAVAILABLE(503,"服务不可用，过载保护");


        private int code;
        private String msg;

        HttpStatusCode(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public  HttpStatusCode fromCode(int code){
            for(HttpStatusCode hsCode:HttpStatusCode.values()){
                if(hsCode.getCode()==code)
                    return hsCode;
            }
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
