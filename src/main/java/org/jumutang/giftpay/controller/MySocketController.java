package org.jumutang.giftpay.controller;

import com.alibaba.fastjson.JSONObject;
import org.jumutang.giftpay.web.socket.MyWebSocketHandshakeInterceptor;
import org.jumutang.giftpay.web.socket.MyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * @author chencq
 * @date 2017/6/28
 **/
@Controller
@RequestMapping("/api/socket")
public class MySocketController {

    private static Logger log = LoggerFactory.getLogger(MySocketController.class);

    @RequestMapping("/web/connection")
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        /*ApplicationContext context = WebApplicationContextUtils
                .getWebApplicationContext(req.getServletContext());
        WebSocketHttpRequestHandler handler = new WebSocketHttpRequestHandler(
                context.getBean("myHandler", MyHandler.class));
        List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
        interceptors.add(context.getBean("myInterceptor",
                MyWebSocketHandshakeInterceptor.class));
        handler.setHandshakeInterceptors(interceptors);
        handler.handleRequest(req, resp);
        log.info("socket connection success");*/

        ApplicationContext context = WebApplicationContextUtils
                .getWebApplicationContext(req.getServletContext());
        WebSocketHttpRequestHandler handler = new WebSocketHttpRequestHandler(new MyHandler());
        List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
        interceptors.add(new MyWebSocketHandshakeInterceptor());
        handler.setHandshakeInterceptors(interceptors);
        handler.handleRequest(req, resp);
    }


}
