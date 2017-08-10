package org.jumutang.giftpay.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

//@Service
public class MyHandler extends TextWebSocketHandler {


    private static Logger logger = LoggerFactory.getLogger(MyHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        logger.info("connection established");
        WebSocketSessionUtil.add(getUserNameFromSession(session), session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        logger.info("receive a message." + message);

        WebSocketSessionUtil.broadcast(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        WebSocketSessionUtil.remove(getUserNameFromSession(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus closeStatus) throws Exception {
        logger.info("conection closed." + closeStatus);

        WebSocketSessionUtil.add(getUserNameFromSession(session), session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private String getUserNameFromSession(WebSocketSession session) {
        Map<String, Object> params = session.getAttributes();

        return params.get("userName").toString();
    }

}

