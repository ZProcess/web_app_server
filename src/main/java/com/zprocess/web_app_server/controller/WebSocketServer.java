package com.zprocess.web_app_server.controller;



import com.alibaba.fastjson.JSON;
import com.zprocess.web_app_server.utils.RedisTool;
import com.zprocess.web_app_server.vo.WebsocketVo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import java.util.LinkedHashMap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Component
@ServerEndpoint("/websocket/{sid}/{fid}")
public class WebSocketServer {
    static Log log=LogFactory.getLog(WebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。

    private  static RedisTool redisTool;

    @Autowired
    public void setRabbitAdmin(RedisTool redisTool){
        WebSocketServer.redisTool = redisTool;
    }

    private static Lock lock = new ReentrantLock();

    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //记录客户端连接顺序
    private static ConcurrentHashMap<String,LinkedHashMap<String,Object>> concurrentHashMap = new ConcurrentHashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid="";

    //接收sid
    private String fid="";
    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("sid") String sid,@PathParam("fid") String fid) {
        this.session = session;
        webSocketSet.add(this); //加入set中
        addOnlineCount(); //在线数加1
        log.info("有新窗口开始监听:"+sid+",当前在线人数为" + getOnlineCount());
        this.sid = sid;
        this.fid = fid;
        WebsocketVo websocketVo = new WebsocketVo();
        try {
            while (true){
                if(!redisTool.hasKey(fid)){
                    websocketVo.setE("1");
                    sendInfo(JSON.toJSONString(websocketVo),sid);
                    websocketVo.setE("0");
                    sendInfoToOthers(JSON.toJSONString(websocketVo),sid);
                    redisTool.set(fid,sid,60);
                    break;
                }else {
                    websocketVo.setE("0");
                    sendInfo(JSON.toJSONString(websocketVo),sid);
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); //从set中删除
        subOnlineCount(); //在线数减1
        try {
            if(redisTool.hasKey(fid)){
                if(sid.equals(redisTool.get(fid))){
                    redisTool.del(fid);
                }
            }

        }catch (Exception e){

        }

        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自窗口"+sid+"的信息:"+message);
        //群发消息
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        if(redisTool.hasKey(fid)){
            if(sid.equals(redisTool.get(fid))){
                redisTool.del(fid);
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        try {
            if(redisTool.hasKey(fid)){
                if(sid.equals(redisTool.get(fid))){
                    redisTool.del(fid);
                }
            }
        }catch (Exception e){

        }
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message,@PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口"+sid+"，推送内容:"+message);
        for (WebSocketServer item : webSocketSet) {
            try {
            //这里可以设定只推送给这个sid的，为null则全部推送
                if(sid==null) {
                    item.sendMessage(message);
                }else if(item.sid.equals(sid)){
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfoToOthers(String message,@PathParam("sid") String sid) throws IOException {
        log.info("sendInfoToOthers--推送消息到窗口"+sid);
        for (WebSocketServer item : webSocketSet) {
            try {
                if(item.sid.equals(sid)){
                    continue;
                }else {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
