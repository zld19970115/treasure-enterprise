package io.treasure.enm;

public interface EGeocode {

    public enum RequestLocal{

        REGEO_KEY(1,"key"),       //CREATE_ORDER
        REGEO_LOCATION(2,"location"),       //传入内容规则：经度在前，纬度在后，经纬度间以“,”分割，经纬度小数点后不要超过 6 位。如果需要解析多个经纬度的话，请用"|"进行间隔，并且将 batch 参数设置为 true，最多支持传入 20 对坐标点。每对点坐标之间用"|"分割。
        regeo_POITYPE(3,"poitype"),       //附近poi类型
        regeo_RADIUS(4,"radius"),        //0~3000默认1000
        regeo_EXTENSIONS(5,"extensions"),
        regeo_BATCH(6,"batch"),
        regeo_ROADLEVEL(7,"roadlevel"),
        regeo_SIG(8,"sig"),
        regeo_OUTPUT(9,"output"),        //默认 JSON
        regeo_CALLBACK(10,"callback"),       //DETACH_ITEM
        regeo_HOMEORCORP(11,"homeorcorp");


        private int code;
        private String paramsField;

        RequestLocal(int code,String paramsField){
            this.code = code;
            this.paramsField = paramsField;
        }

        public int getCode(){
            return this.code;
        }
        public String getParamsField(){
            return this.paramsField;
        }
    }

    public enum ResponseLocalInfo{

        REGEO_STATUS(1,"status"),       //CREATE_ORDER
        REGEO_INFO(2,"info"),       //ATTACH_ROOM
        REGEO_CODES(3,"regeocodes"),        //REFUND_ORDER
        REGEO_CODES__FORMATTED_ADDRESS(4,"formatted_address"),       //DETACH_ITEM
        REGEO_CODES__ADDRESS_COMPONENT(5,"addressComponent"),
        _ADDRESS_COMPONENT__PROVINCE(6,"province"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__CITY(7,"city"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__CITYCODE(8,"citycode"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__DISTRICT(9,"district"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__ADCODE(10,"adcode"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__TOWNSHIP(11,"township"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__TOWNCODE(12,"towncode"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__BUILDING(13,"building"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__STREETNUMBER(14,"streetNumber"),       //DETACH_ITEM
        _ADDRESS_COMPONENT__BUSINESSAREAS(15,"businessAreas");       //DETACH_ITEM
        //返回信息内容较多，仅返回到此处，后有需求请链https://lbs.amap.com/api/webservice/guide/api/georegeo/

        private int code;
        private String paramsField;

        ResponseLocalInfo(int code,String paramsField){
            this.code = code;
            this.paramsField = paramsField;
        }

        public int getCode(){
            return this.code;
        }
        public String getParamsField(){
            return this.paramsField;
        }
    }



}
