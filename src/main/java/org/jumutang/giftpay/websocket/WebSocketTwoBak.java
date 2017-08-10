package org.jumutang.giftpay.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jumutang.giftpay.entity.OilContant;
import org.jumutang.giftpay.entity.RoomRecord;
import org.jumutang.giftpay.entity.UserModel;
import org.jumutang.giftpay.tools.RequestTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author uptop
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint(value = "/websocketTwoBak/{param}", configurator = GetHttpSessionConfigurator.class)
public class WebSocketTwoBak {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static CopyOnWriteArraySet<WebSocketTwoBak> webSocketSet = new CopyOnWriteArraySet<WebSocketTwoBak>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //HttpSession可以用来保存信息 1.room 2.oil
    private HttpSession httpSession;

    //线程安全的map来存放房间和对应的人数
    public static ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketTwoBak>> concurrentHashMap = new ConcurrentHashMap();

    //当前房间已抢到油数
    public static ConcurrentHashMap<String, AtomicInteger> oilHashMap = new ConcurrentHashMap();

    //设置当前房间的油数
    public static ConcurrentHashMap<String, Integer> oilControlMap = new ConcurrentHashMap<String, Integer>();


    public static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> userOilMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();

    Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
   /* @OnOpen
    public void onOpen(@PathParam(value="param") String param,Session session, EndpointConfig config) {
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.error("当前在线人数为:"+getOnlineCount());
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties()
                .get(HttpSession.class.getName());
        String roomId= (String) httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID);
        UserModel userModel= (UserModel) httpSession.getAttribute(OilContant.SESSION_USERMODEL);
        if(userModel==null){
            webSocketSet.remove(this);  //从set中删除
            subOnlineCount();           //在线数减1
            concurrentHashMap.get(this.httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID)).remove(this);
            logger.error("有一连接关闭！当前在线人数为" + getOnlineCount());
        }else{
            httpSession.setAttribute(OilContant.SESSION_USERMODEL,userModel);
        }
        logger.error("获取房间号:"+roomId+",该用户信息:"+JSONObject.toJSONString(userModel));
        if(StringUtils.isEmpty(userModel.getClickNum())||userModel.getClickNum()==null){
            userModel.setClickNum(OilContant.ZERO);
        }
        logger.error("用户点击数为:"+JSONObject.toJSONString(userModel));
        this.httpSession.setAttribute(OilContant.SESSION_ROOMRECORDID,roomId);
        JSONArray jsonArray = new JSONArray();
        logger.error("总数据添加该数据");

        //初始化房间
        if(concurrentHashMap.get(roomId)==null){
            concurrentHashMap.put(roomId,new CopyOnWriteArraySet<WebSocketTwo>());
        }
        concurrentHashMap.get(roomId).add(this);
        //初始化房间油数
        if(oilHashMap.get(roomId)==null){
            oilHashMap.put(roomId,new AtomicInteger(0));
        }
        //初始化用户油滴数
        if(userOilMap.get(roomId).get(OilContant.SESSION_OPENID)==null){
            userOilMap.get(roomId).put(OilContant.SESSION_OPENID,0);
        }else{
            int userOilNum = userOilMap.get(roomId).get(OilContant.SESSION_OPENID);
            this.httpSession.setAttribute("oilNum",userOilNum);
        }

        for (WebSocketTwo item : concurrentHashMap.get(this.httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID))) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put(OilContant.SESSION_OPENID,userModel.getOpenId());
            jsonObject1.put(OilContant.SESSION_HEADIMG,userModel.getHeadImg());
            jsonObject1.put("nickName",userModel.getNickName());
            jsonObject1.put("rate","20");
            jsonObject1.put("clickNum",userModel.getClickNum());
            logger.error("添加数据为:"+jsonObject1.toJSONString());
            jsonArray.add(jsonObject1);
        }

        logger.error("总数据为:"+jsonArray.toJSONString());
        JSONObject returnObj = new JSONObject();
        returnObj.put("type",0);
        returnObj.put("totalOilNum",oilControlMap.get(OilContant.SESSION_ROOMRECORDID)-oilHashMap.get(OilContant.SESSION_ROOMRECORDID).get());
        returnObj.put("jsonArray",jsonArray);
        String returnStr = JSON.toJSONString(returnObj);
        logger.error("准备向房间号:"+roomId+"发送数据");
        for (WebSocketTwo item : concurrentHashMap.get(this.httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID))) {
            try {
                synchronized (item){
                    item.session.getBasicRemote().sendText(returnStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                webSocketSet.remove(this);  //从set中删除
                subOnlineCount();           //在线数减1
                concurrentHashMap.get(this.httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID)).remove(this);
                System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
                try {
                    item.session.close();
                } catch (IOException e1) {
                    logger.error("发送数据出错");
                }
                continue;
            }
        }
    }
*/

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "param") String param, Session session, EndpointConfig config) {
        //设置总webSocket属性
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.error("有新连接加入！当前总在线人数为" + getOnlineCount());
        this.httpSession = (HttpSession) config.getUserProperties()
                .get(HttpSession.class.getName());
        String room = (String) httpSession.getAttribute(OilContant.SESSION_ROOMRECORDID);
        logger.error("房间ID:" + room);
        UserModel userModel = (UserModel) httpSession.getAttribute(OilContant.SESSION_USERMODEL);
        logger.error("用户信息:" + JSONObject.toJSONString(userModel));
        String openId = userModel.getOpenId();
        RoomRecord roomRecord = (RoomRecord) httpSession.getAttribute("roomData");
        logger.error("房间信息:" + JSONObject.toJSONString(roomRecord));
        //设置用户session相关属性
        this.session = session;
        this.httpSession.setAttribute("room", room);
        this.httpSession.setAttribute("openId", openId);
        this.httpSession.setAttribute("oilNum", 0);
        logger.error("房间号为" + room + ",openId为" + openId + "进入房间!");
        if (concurrentHashMap.get(room) == null) {
            concurrentHashMap.put(room, new CopyOnWriteArraySet<WebSocketTwoBak>());
        }
        concurrentHashMap.get(room).add(this);
        if (oilHashMap.get(room) == null) {
            oilHashMap.put(room, new AtomicInteger(0));
        }
        if (userOilMap.get(room) == null) {
            userOilMap.put(room, new ConcurrentHashMap<String, Integer>());
        }
        if (oilControlMap.get(room) == null) {
            oilControlMap.put(room, Integer.valueOf(roomRecord.getOilNum()));
        }
        if (userOilMap.get(room).get(openId) == null) {
            userOilMap.get(room).put(openId, 0);
        } else {
            int userOilNum = userOilMap.get(room).get(openId);
            this.httpSession.setAttribute("oilNum", userOilNum);
        }
        JSONArray jsonArray = new JSONArray();
        int sumNum=0;
        for (WebSocketTwoBak item : concurrentHashMap.get(this.httpSession.getAttribute("room"))) {
            logger.error("获取各个用户的油礼付信息");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("openId",item.httpSession.getAttribute("openId"));
            jsonObject1.put("oilNum",item.httpSession.getAttribute("oilNum"));
            sumNum+=(Integer)item.httpSession.getAttribute("oilNum");
            logger.error("****"+jsonObject1+"******");
            jsonArray.add(jsonObject1);
        }
        JSONObject returnObj = new JSONObject();
        logger.error("收集广播数据=====");
        returnObj.put("oilNum", this.httpSession.getAttribute("oilNum"));
        returnObj.put("type", 0);
        returnObj.put("jsonArray", jsonArray);
        returnObj.put("name", "baseInfo");
        returnObj.put("totalOilNum", oilControlMap.get(room) - sumNum);
        returnObj.put("userData", JSONObject.toJSONString(userModel));
        returnObj.put("roomData", JSONObject.toJSONString(roomRecord));
        logger.error("广播数据为:"+returnObj.toJSONString());
        String returnStr = JSON.toJSONString(returnObj);
        for (WebSocketTwoBak item : concurrentHashMap.get(this.httpSession.getAttribute("room"))) {
            logger.error("向各个房间广播消息");
            try {
                synchronized (item) {
                    logger.error("准备发送消息:"+returnStr);
                    item.session.getBasicRemote().sendText(returnStr);
                }
            } catch (Exception e) {
                logger.error("发送消息错误:" + e.toString());
                e.printStackTrace();
                webSocketSet.remove(this);  //从set中删除
                subOnlineCount();           //在线数减1
                concurrentHashMap.get(this.httpSession.getAttribute("room")).remove(this);
                logger.error("有一连接关闭！当前在线人数为" + getOnlineCount());
                try {
                    item.session.close();
                } catch (IOException e1) {
                    logger.error("关闭session错误");
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
        subOnlineCount();           //在线数减1
        concurrentHashMap.get(this.httpSession.getAttribute("room")).remove(this);
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
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
        logger.error("发送错误:" + error.toString());
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) {
        logger.error("来自客户端的消息:" + message);
        logger.error("当前房间号:" + this.httpSession.getAttribute("room"));
        String returnStr = "";
        JSONObject mesdata = JSONObject.parseObject(message);
        logger.error("****" + message);
        if ((mesdata.getString("name")).equals("catch")) {
            oilHashMap.get(this.httpSession.getAttribute("room")).getAndIncrement();
            this.httpSession.setAttribute("oilNum", Integer.parseInt(String.valueOf(this.httpSession.getAttribute("oilNum"))) + mesdata.getInteger("desc"));
            userOilMap.get(this.httpSession.getAttribute("room")).put(String.valueOf(this.httpSession.getAttribute("openId")), Integer.parseInt(String.valueOf(this.httpSession.getAttribute("oilNum"))));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("openId", this.httpSession.getAttribute("openId"));
            jsonObject.put("oilNum", this.httpSession.getAttribute("oilNum"));
            jsonObject.put("type", 1);
            jsonObject.put("name", "catch");
            jsonObject.put("desc", mesdata.getInteger("desc"));
            JSONArray jsonArray=new JSONArray();
            int sumNum=0;
            for (WebSocketTwoBak item : concurrentHashMap.get(this.httpSession.getAttribute("room"))) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("openId",item.httpSession.getAttribute("openId"));
                jsonObject1.put("oilNum",item.httpSession.getAttribute("oilNum"));
                sumNum+=(Integer) item.httpSession.getAttribute("oilNum");
                jsonArray.add(jsonObject1);
            }
            jsonObject.put("totalOilNum", oilControlMap.get(this.httpSession.getAttribute("room")) - sumNum);
            jsonObject.put("jsonArray",jsonArray);
            returnStr = JSON.toJSONString(jsonObject);
        }
        //群发消息
        for (WebSocketTwoBak item : concurrentHashMap.get(this.httpSession.getAttribute("room"))) {
            try {
                synchronized (item) {
                    item.session.getBasicRemote().sendText(returnStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                webSocketSet.remove(this);  //从set中删除
                subOnlineCount();           //在线数减1
                concurrentHashMap.get(this.httpSession.getAttribute("room")).remove(this);
                logger.error("有一连接关闭！当前在线人数为" + getOnlineCount());
                try {
                    item.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketTwoBak.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketTwoBak.onlineCount--;
    }

    //检查房间是否有效
    public JSONObject ParamToRoomAndOpenId(String stringParam) {
        Map<String, String> requestParamMap = RequestTool.RequestParam(stringParam);
        String room = requestParamMap.get("room");
        String openId = requestParamMap.get("openId");

        if (StringUtils.isEmpty(room) || StringUtils.isEmpty(openId)) {
            return null;
        } else {
            if (!StringUtils.isEmpty(requestParamMap.get("totalOilNum"))) {
                int totalOilNum = Integer.parseInt(requestParamMap.get("totalOilNum"));
                oilControlMap.put(room, totalOilNum);
                oilHashMap.put(room, new AtomicInteger(0));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("room", room);
            jsonObject.put("openId", openId);
            return jsonObject;
        }
    }

    //推送给所有人信息
    public void sendAllMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public void sendAllMsg(String msg) {
        for (WebSocketTwoBak item : webSocketSet) {
            try {
                item.sendAllMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
