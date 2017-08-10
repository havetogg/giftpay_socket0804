package org.jumutang.giftpay.web.socket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

//@Service
public class MyWebSocketHandshakeInterceptor extends
        HttpSessionHandshakeInterceptor {

    private static Logger logger = LoggerFactory.getLogger(MyWebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                   ServerHttpResponse serverHttpResponse, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        logger.info("hi get request.");

        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest request = servletRequest.getServletRequest();

        String userName = request.getParameter("userName");
        attributes.put("userName", userName);

        logger.info("a client userName=" + userName);

        super.beforeHandshake(serverHttpRequest, serverHttpResponse, wsHandler,
                attributes);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

}

