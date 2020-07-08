package io.treasure.push;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.AbstractTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * @author user
 * @title: 1
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/2915:15
 */
public class AppPushUtil {

    // STEP1：获取应用基本信息
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";
    public static void main(String[] args) throws IOException {
        //String clientId="bed827bcb12f99ebb004180ee0cfa73d";
        //String clientId="42a6ceff19d73a608bc2cbf61ed0d86b";
        String clientId="eb55b5961ab55896101ec7137c2d7fbd";
        pushToSingleMer("订单管理","已经接单",clientId);
    }

    private static APNPayload getAPNPayload(String text) {
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
//        payload.setAutoBadge("-1");
        payload.setContentAvailable(1);//推送直接带有透传数据
        //ios 12.0 以上可以使用 Dictionary 类型的 sound
        payload.setSound("default");
        payload.setCategory("$由客户端定义");//在客户端通知栏触发特定的action和button显示
        payload.addCustomMsg("由客户自定义消息key", "由客户自定义消息value");//增加自定义的数据,Key-Value形式

        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(text));//通知消息体 SimpleAlertMsg: 	通知文本消息字符串
//        payload.setAlertMsg(getDictionaryAlertMsg());  //字典模式使用APNPayload.DictionaryAlertMsg

        //设置语音播报类型，int类型，0.不可用 1.播放body 2.播放自定义文本
//        payload.setVoicePlayType(2);
        //设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
        //注：当"定义类型"=2, "定义内容"为空时则忽略不播放
//        payload.setVoicePlayMessage("定义内容");//设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效

        // 添加一个多媒体资源，当前最多传入3个资源
//        payload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.pic)
//                .setResUrl("资源文件地址")
//                .setOnlyWifi(true));

        return payload;
    }


    public static TransmissionTemplate getTransmissionTemplate(String title, String text) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(AppInfo.APPID_MERCHANT);
        template.setAppkey(AppInfo.APPKEY_MERCHANT);
        template.setTransmissionType(1);//搭配transmissionContent使用，可选值为1、2；1：立即启动APP（不推荐使用，影响客户体验）2：客户端收到消息后需要自行处理
        template.setTransmissionContent("{\"title\": \""+title+"\",\"content\": \""+text+"\",\"payload\": \"lixian\"} "); //透传内容,不支持转义字符
        template.setAPNInfo(getAPNPayload(text)); //ios消息推送，用于设置标题、内容、语音、多媒体、VoIP（基于IP的语音传输）等。离线走APNs时起效果
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(text);
        notify.setIntent("intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=io.jubao.UNI809BFD1/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title="+title+";S.content="+text+";S.payload=lixian;end");
//        notify.setIntent("intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=io.jubao.UNI809BFD1;S.UP-OL-SU=true;S.title="+title+";S.content="+text+";S.payload=test;end");
        notify.setType(GtReq.NotifyInfo.Type._intent);
        template.set3rdNotifyInfo(notify);//设置第三方通知


        return template;
    }

    private static SingleMessage getSingleMessage(AbstractTemplate template) {
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        message.setPriority(1);

        /**
         * !!!2020年01月16日16时34分16秒(星期四) GitHub上这里的代码有坑，透传消息setPushNetWorkType为1的话，客户端连着wifi推送不到!!!
         * 官网注释：
         *  推送网络要求
         *  0:联网方式不限;
         *  1:仅wifi;
         *  2:仅移动网络
         *
         */
        message.setPushNetWorkType(0); // 判断客户端是否wifi环境下推送。1为仅在wifi环境下推送，0为不限制网络环境，默认不限
        return message;
    }



    public static void pushToSingleMerchant(String title,String text,String CID) {
        AbstractTemplate template = getTransmissionTemplate(title,text); //透传消息模版
        IGtPush push = new IGtPush(url,AppInfo.APPKEY_MERCHANT, AppInfo.MASTERSECRET_MERCHANT);
        SingleMessage message = getSingleMessage(template);
        Target target = new Target();
        target.setAppId(AppInfo.APPID_MERCHANT);
        target.setClientId(CID);
        AppMessage message1 = new AppMessage();
        message1.setData(template);
        message1.setAppIdList(Collections.singletonList(AppInfo.APPID_MERCHANT));
        message1.setOffline(true);
        message1.setOfflineExpireTime(1000 * 600);// 时间单位为毫秒
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
            // STEP6：执行推送
//            ret = push.pushMessageToApp(message1,CID);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
        } else {
            System.out.println("服务器响应异常");
        }
    }


}
