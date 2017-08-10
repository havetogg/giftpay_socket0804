package org.jumutang.giftpay.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jumutang.giftpay.common.redis.RedisCacheUtil;
import org.jumutang.giftpay.entity.OilContant;
import org.jumutang.giftpay.entity.RoomRecord;
import org.jumutang.giftpay.entity.UserModel;
import org.jumutang.giftpay.tools.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author uptop
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint(value = "/websocketTwo/{param}", configurator = GetHttpSessionConfigurator.class)
public class WebSocketTwo {
    public static CopyOnWriteArraySet<WebSocketTwo> webSocketSet = new CopyOnWriteArraySet<WebSocketTwo>();

    private Session session;

    private HttpSession httpSession;

    private String roomId;

    public static ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketTwo>> concurrentHashMap = new ConcurrentHashMap();

    private static RedisCacheUtil<Object> redisCacheUtil;

    private static Logger logger = LoggerFactory.getLogger(WebSocketTwo.class);


    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "param") String param, Session session, EndpointConfig config) throws IOException {
        webSocketSet.add(this);     //加入set中
        this.session = session;
        if (redisCacheUtil == null) {
            redisCacheUtil = (RedisCacheUtil<Object>) SpringContextUtil.getBean("redisSocketCacheUtil");
        }
        this.httpSession = (HttpSession) config.getUserProperties()
                .get(HttpSession.class.getName());

        String room = (String) httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID);
        String obj = httpSession.getAttribute(OilContant.SESSION_USERMODEL).toString();
        UserModel userModel = JSONObject.parseObject(obj, UserModel.class);
        String openId = userModel.getOpenId();
        obj = httpSession.getAttribute("roomData").toString();
        RoomRecord roomRecord = JSONObject.parseObject(obj, RoomRecord.class);
        this.roomId = room;
//        logger.error("房间信息:" + JSONObject.toJSONString(roomRecord));
//        logger.error("房间ID:" + room);
//        logger.error("用户信息:" + JSONObject.toJSONString(userModel));
//        logger.error("用户openId:" + openId);
        if (concurrentHashMap.get(room) == null) {
            concurrentHashMap.put(room, new CopyOnWriteArraySet<WebSocketTwo>());
        }
        concurrentHashMap.get(room).add(this);
        long userOilNum = redisCacheUtil.incrRedisCacheMap("room:" + room + ":" + openId, 0);
//        logger.error("用户油滴数："+userOilNum);
        String sumOilNum = redisCacheUtil.getCacheObject("room:oilNum:" + room).toString();
//        logger.error("当前用户房间油滴数:"+sumOilNum);
        JSONObject returnObj = new JSONObject();
        returnObj.put("openId", openId);
        returnObj.put("oilNum", userOilNum);
        returnObj.put("nickName", userModel.getNickName());
        returnObj.put("headImg", userModel.getHeadImg());
        returnObj.put("rate", userModel.getRate());
        returnObj.put("name", "baseInfo");
        returnObj.put("totalOilNum", sumOilNum);
        String returnStr = JSON.toJSONString(returnObj);
//        logger.error("发送信息对象："+returnStr);
        for (WebSocketTwo item : concurrentHashMap.get(room)) {
            try {
                synchronized (item) {
//                    logger.error("item的状态是"+item.session.isOpen());
                    if (item.session.isOpen() || "true".equals(item.session.isOpen())) {
//                        item.session.getBasicRemote().setBatchingAllowed(true);
//                        item.session.getBasicRemote().flushBatch();
                        item.session.getBasicRemote().sendText(returnStr);
                    }
                }
            } catch (Exception e) {
                logger.error("ONOPEN异常:" + e.toString());
                webSocketSet.remove(this);  //从set中删除
                concurrentHashMap.get(room).remove(this);
                try {
                    item.session.close();
                } catch (IOException e1) {
                    logger.error("ONOPEN的ItemSession关闭异常:" + e1.toString());
                } finally {
                    continue;
                }
            } finally {
                continue;
            }

        }
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) {
        JSONObject obj = JSONObject.parseObject(message);
        String roomId = obj.getString("roomId");
        String msgType = obj.getString("name");
        this.roomId = roomId;
        String returnStr = "";
        JSONObject jsonObject = new JSONObject();
        if (msgType.equals("catch")) {
            String openId = null;
            String oilNum = null;
            long redisOilNum = 0;
            try {
                openId = obj.getString("openId");
                oilNum = obj.getString("desc");
                redisOilNum = 0L;
                logger.error("获取redis--------------------" + Integer.valueOf(oilNum));
                long nowOilNum = redisCacheUtil.incrRedisCacheMap("room:oilNum:" + roomId, -(Long.valueOf(oilNum)));
                logger.error("当前房间油量：" + nowOilNum);
                if (nowOilNum < 0L) {
                    jsonObject.put("sumOilNum", "0");
                    redisOilNum = Long.valueOf(redisCacheUtil.getCacheMap("room:" + roomId + ":" + openId).toString());
                } else {
                    redisOilNum = redisCacheUtil.incrRedisCacheMap("room:" + roomId + ":" + openId, Integer.valueOf(oilNum));
                    jsonObject.put("sumOilNum", String.valueOf(nowOilNum));
                }
            } catch (NumberFormatException e) {
                logger.error("发送点击抢油:", e);
                e.printStackTrace();
            }
            jsonObject.put("name", "catch");
            jsonObject.put("openId", openId);
            jsonObject.put("oilNum", String.valueOf(redisOilNum));
            jsonObject.put("clickNum", oilNum);
            returnStr = JSON.toJSONString(jsonObject);
        } else if (msgType.equals("content")) {
            jsonObject.put("name", "content");
            jsonObject.put("content", obj.getString("content"));
            returnStr = JSON.toJSONString(jsonObject);
        }
        for (WebSocketTwo item : concurrentHashMap.get(roomId)) {
            try {
//                synchronized (item) {
                if(item.session.isOpen()){
                    item.session.getBasicRemote().sendText(returnStr);
                }
//                }
            } catch (Exception e) {
                e.printStackTrace();
                webSocketSet.remove(this);  //从set中删除
                concurrentHashMap.get(roomId).remove(this);
                try {
                    item.session.close();
                } catch (IOException e1) {
                }
                continue;
            }
        }
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        concurrentHashMap.get(this.roomId).remove(this);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.error("来自客户端的消息:" + message);
        this.sendMessage(message);
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        webSocketSet.remove(this);  //从set中删除
        concurrentHashMap.get(this.roomId).remove(this);
        logger.error("发送错误:", error);
    }
}

