package com.chijey.startup.common.config;

import com.chijey.startup.security.service.MessageService;
import com.chijey.startup.security.service.WebSocketServer;
import com.chijey.startup.security.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author xxs
 * @Date 2020/4/24 15:41
 */
@Configuration
public class WebSocketConfig {
    /**
     * ServerEndpointExporter 作用
     *
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setMessageService(MessageService messageService){
        WebSocketServer.messageService = messageService;
    }
    @Autowired
    public void setJwtTokenUtils(JwtTokenUtils jwtTokenUtils){
        WebSocketServer.jwtTokenUtils = jwtTokenUtils;
    }
}
