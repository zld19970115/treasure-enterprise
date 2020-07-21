package io.treasure.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.java_websocket.WebSocket;
import com.alipay.api.java_websocket.handshake.ClientHandshake;
import com.alipay.api.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class MyWebScoket extends WebSocketServer {

    public MyWebScoket() throws UnknownHostException {
        super();
    }

    public MyWebScoket(int port)  {
        super(new InetSocketAddress(port));
    }

    public MyWebScoket(InetSocketAddress address) {
        super(address);
    }



    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // ws连接的时候触发的代码，onOpen中我们不做任何操作
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //断开连接时候触发代码
        userLeave(conn);
        System.out.println(reason);
    }
    @Autowired
    WsPool wsPool;
    @Override
    public void onMessage(WebSocket conn, String message) {
        //有用户连接进来
       // Map<String, String> obj =  (Map<String,String>) JSONObject.parse(message);
        System.out.println(conn);
        System.out.println(message);
     //  String username = obj.get("name");
        userJoin(conn,  message);
        wsPool.sendMessageToAll("用户连接。。。到了");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //错误时候触发的代码
        System.out.println("on error");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {

    }

    /**
     * 去除掉失效的websocket链接
     */
    private void userLeave(WebSocket conn){
        WsPool.removeUser(conn);
    }
    /**
     * 将websocket加入用户池
     * @param conn
     * @param userName
     */
    private void userJoin(WebSocket conn,String userName){
        WsPool.addUser(userName, conn);
    }
}