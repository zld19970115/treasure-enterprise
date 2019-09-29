package io.treasure.push;


import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.AbstractTemplate;


import java.io.IOException;


public class AppPushUtil {
    // STEP1：获取应用基本信息
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";
    public static void main(String[] args) throws IOException {
        //String clientId="bed827bcb12f99ebb004180ee0cfa73d";
        //String clientId="42a6ceff19d73a608bc2cbf61ed0d86b";
        String clientId="42a6ceff19d73a608bc2cbf61ed0d86b";
        pushToSingleClient("订单管理","已经接单","",clientId);
    }


    /**
     * 客户端对单个用户推送消息
     *
     * 场景1：某用户发生了一笔交易，银行及时下发一条推送消息给该用户。
     *
     * 场景2：用户定制了某本书的预订更新，当本书有更新时，需要向该用户及时下发一条更新提醒信息。
     * 这些需要向指定某个用户推送消息的场景，即需要使用对单个用户推送消息的接口。
     * title 标题
     * text 内容
     * logo 图标
     * masterSect
     * clientId
     */
    public  static void pushToSingleClient(String title,String text,String logo,String clientId) {
        IGtPush push = new IGtPush(AppInfo.APPKEY_CLIENT, AppInfo.MASTERSECRET_CLIENT);
        AbstractTemplate template = PushTemplate.getNotificationTemplate(AppInfo.APPID_CLIENT,AppInfo.APPKEY_CLIENT,title,text,logo); //通知模板(点击后续行为: 支持打开应用、发送透传内容、打开应用同时接收到透传 这三种行为)
//        AbstractTemplate template = PushTemplate.getLinkTemplate(); //点击通知打开(第三方)网页模板
//        AbstractTemplate template = PushTemplate.getTransmissionTemplate(); //透传消息模版
//        AbstractTemplate template = PushTemplate.getRevokeTemplate(); //消息撤回模版
//        AbstractTemplate template = PushTemplate.getStartActivityTemplate(); //点击通知, 打开（自身）应用内任意页面

        // 单推消息类型
        SingleMessage message = getSingleMessage(template);
        Target target = new Target();
        target.setAppId(AppInfo.APPID_CLIENT);
        target.setClientId(clientId);
//        target.setAlias(ALIAS); //别名需要提前绑定
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
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

    /**
     * 商户端对单个用户推送消息
     *
     * 场景1：某用户发生了一笔交易，银行及时下发一条推送消息给该用户。
     *
     * 场景2：用户定制了某本书的预订更新，当本书有更新时，需要向该用户及时下发一条更新提醒信息。
     * 这些需要向指定某个用户推送消息的场景，即需要使用对单个用户推送消息的接口。
     * title 标题
     * text 内容
     * logo 图标
     * masterSect
     * clientId
     */
    public  static void pushToSingleMerchant(String title,String text,String logo,String clientId) {
        IGtPush push = new IGtPush(AppInfo.APPKEY_MERCHANT, AppInfo.MASTERSECRET_MERCHANT);
        AbstractTemplate template = PushTemplate.getNotificationTemplate(AppInfo.APPID_MERCHANT,AppInfo.APPKEY_MERCHANT,title,text,logo); //通知模板(点击后续行为: 支持打开应用、发送透传内容、打开应用同时接收到透传 这三种行为)
//        AbstractTemplate template = PushTemplate.getLinkTemplate(); //点击通知打开(第三方)网页模板
//        AbstractTemplate template = PushTemplate.getTransmissionTemplate(); //透传消息模版
//        AbstractTemplate template = PushTemplate.getRevokeTemplate(); //消息撤回模版
//        AbstractTemplate template = PushTemplate.getStartActivityTemplate(); //点击通知, 打开（自身）应用内任意页面

        // 单推消息类型
        SingleMessage message = getSingleMessage(template);
        Target target = new Target();
        target.setAppId(AppInfo.APPID_MERCHANT);
        target.setClientId(clientId);
//        target.setAlias(ALIAS); //别名需要提前绑定
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
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


    private static SingleMessage getSingleMessage(AbstractTemplate template) {
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        message.setPriority(1);
        message.setPushNetWorkType(0); // 判断客户端是否wifi环境下推送。1为仅在wifi环境下推送，0为不限制网络环境，默认不限
        return message;
    }
}
