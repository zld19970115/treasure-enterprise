package io.treasure.utils;

import com.alipay.api.java_websocket.WebSocket;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WsPool {
    private static final Map<WebSocket, String> wsUserMap = new HashMap<WebSocket, String>();

    /**
     * 通过websocket连接获取其对应的用户
     */
    public static String getUserByWs(WebSocket conn) {
        return wsUserMap.get(conn);
    }

    /**
     * 根据userName获取WebSocket,这是一个list,此处取第一个
     * 因为有可能多个websocket对应一个userName（但一般是只有一个，因为在close方法中，我们将失效的websocket连接去除了）
     */
    public static WebSocket getWsByUser(String userName) {
        Set<WebSocket> keySet = wsUserMap.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String cuser = wsUserMap.get(conn);
                if (cuser.equals(userName)) {
                    return conn;
                }
            }
        }
        return null;
    }

    /**
     * 向连接池中添加连接
     */
    public static void addUser(String userName, WebSocket conn) {
        wsUserMap.put(conn, userName); // 添加连接
    }

    /**
     * 获取所有连接池中的用户，因为set是不允许重复的，所以可以得到无重复的user数组
     */
    public static Collection<String> getOnlineUser() {
        List<String> setUsers = new ArrayList<String>();
        Collection<String> setUser = wsUserMap.values();
        for (String u : setUser) {
            setUsers.add(u);
        }
        return setUsers;
    }

    /**
     * 移除连接池中的连接
     */
    public static boolean removeUser(WebSocket conn) {
        if (wsUserMap.containsKey(conn)) {
            wsUserMap.remove(conn); // 移除连接
            return true;
        } else {
            return false;
        }
    }

    /**
     * 向特定的用户发送数据
     */
    public static void sendMessageToUser(WebSocket conn, String message) {
        if (null != conn && null != wsUserMap.get(conn)) {
            conn.send(message);
        }
    }

    /**
     * 向所有用户名中包含某个特征得用户发送消息
     */
    public static void sendMessageToSpecialUser(String message,String special) {
        Set<WebSocket> keySet = wsUserMap.keySet();
        if (special == null) {
            special = "";
        }
        synchronized (keySet) {
            for (WebSocket conn:keySet) {
                String user = wsUserMap.get(conn);
                try {
                    if (user != null) {
                        String [] cus = user.split("_");
                        if (!StringUtils.isNullOrEmpty(cus[0])) {
                            String cusDot = "," + cus[0] + ",";
                            if (cusDot.contains(","+special+",")) {
                                conn.send(message);
                            }
                        }else {
                            conn.send(message);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    //wsUserMap.remove(conn);
                }
            }

        }
    }
    /**
     * 向所有的用户发送消息
     */
    public static void sendMessageToAll(String message) {
        Set<WebSocket> keySet = wsUserMap.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String user = wsUserMap.get(conn);
                if (user != null) {
                    conn.send(message);
                }
            }
        }
    }


}