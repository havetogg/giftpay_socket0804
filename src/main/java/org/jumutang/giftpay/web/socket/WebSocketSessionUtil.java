package org.jumutang.giftpay.web.socket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketSessionUtil {

    private static Map<String, WebSocketSession> clients = new ConcurrentHashMap<String, WebSocketSession>();

    public static void add(String userName, WebSocketSession session) {
        clients.put(userName, session);
    }

    public static WebSocketSession get(String userName) {
        return clients.get(userName);
    }

    public static void remove(String userName) {
        clients.remove(userName);
    }

    public static void broadcast(TextMessage message)
            throws Exception {

        Set<String> allUsers = clients.keySet();

        for (String name : allUsers) {
            WebSocketSession session = clients.get(name);
            session.sendMessage(message);
        }
    }
}

