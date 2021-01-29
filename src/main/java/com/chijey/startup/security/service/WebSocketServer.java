package com.chijey.startup.security.service;


import com.alibaba.fastjson.JSON;
import com.chijey.startup.security.config.JwtSecurityProperties;
import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.service.dto.SendMess;
import com.chijey.startup.security.utils.JwtTokenUtils;
import com.chijey.startup.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/webSocket/{openId}")
@Component
@Slf4j
public class WebSocketServer {
    public static JwtTokenUtils jwtTokenUtils;
    public static MessageService messageService;

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessageByCmd(Session session, String cmd, Object obj) throws IOException {
        SendMess mess = new SendMess();
        mess.setCmd(cmd);
        mess.setData(obj);
        sendMessage(session, JSON.toJSONString(mess));
    }
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }
    //给指定用户发送信息
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session,@PathParam(value = "openId")String openId){
        JwtSecurityProperties jwtSecurityProperties = SpringContextHolder.getBean(JwtSecurityProperties.class);
        String token = null;
        String bearerToken = token;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtSecurityProperties.getTokenStartWith())) {
            token = bearerToken.substring(jwtSecurityProperties.getTokenStartWith().length());
        }
        if (StringUtils.hasText(token) && jwtTokenUtils.validateToken(token)) {
            Authentication authentication = jwtTokenUtils.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                openId = authentication.getName();
                log.debug("set Authentication to security context for '{}', websocket: {}", authentication.getName());
            }
        } else {
            log.debug("no valid JWT token found, websocket token : {}", token);
        }

        sessionPools.put(openId, session);
        addOnlineCount();
        System.out.println(openId + "加入webSocket！当前人数为" + onlineNum);
        List<String> userIds = new ArrayList();
        Enumeration<String> keys = sessionPools.keys();
        while(keys.hasMoreElements()){
            String value = keys.nextElement();//调用nextElement方法获得元素
            userIds.add(value);
        }
        System.out.println("=============当前用户：" + userIds.toString());//打印当前参与聊天人数
//        for(Session s:sessionPools.values()){
//            try {
////                sendMessageByCmd(s,"userList",userIds);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "openId") String openId){
        sessionPools.remove(openId);
        subOnlineCount();
        System.out.println(openId + "断开webSocket连接！当前人数为" + onlineNum);
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message,Session session) throws IOException{
        if("ping".equals(message)){
            log.info("接受到心跳..");
            sendMessageByCmd(session,"log","pong");
            return;
        }
        SendMess u = JSON.parseObject(message, SendMess.class);
        System.out.println("客户端：" + message + ",已收到");
        String toUserId = u.getToUserId();
        session = sessionPools.get(toUserId);
        Message msg = new Message();
        BeanUtils.copyProperties(message,msg);
        msg.setData(u.getData().toString());
        msg.setChatId(generateChatID(toUserId,u.getSenderOpenId()));
        msg.setContentType("txt");
        msg.setCmd("log");
        msg.setCreateTime(new Date());
        msg.setId(UUID.randomUUID().toString());
        msg.setSenderOpenId(u.getSenderOpenId());
        msg.setToUserId(u.getToUserId());
        messageService.save(msg);
        //只发给指定人
        if(session!=null){
            try {
                sendMessageByCmd(session,u.getCmd(),u.getData());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    private String generateChatID(String toUserId, String senderOpenId) {
        if(toUserId ==null |senderOpenId == null){
            throw new RuntimeException("聊天对象ID不能为空 toUserId:"+toUserId+"  senderOpenId="+senderOpenId);
        }
        List<String> ids = Arrays.asList(toUserId,senderOpenId);
        Collections.sort(ids);
        return  ids.get(0)+ids.get(1);
    }



}
