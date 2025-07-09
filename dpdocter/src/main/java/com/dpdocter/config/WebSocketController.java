//package com.dpdocter.config;
//
//import java.security.Principal;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.websocket.OnMessage;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketController {
//
//    private final SimpMessagingTemplate template;
//
//    @Autowired
//    WebSocketController(SimpMessagingTemplate template){
//        this.template = template;
//    }
//
//    @MessageMapping("/send/message")
//    public void onReceivedMesage(String message){
//        this.template.convertAndSend("/chat",  new SimpleDateFormat("HH:mm:ss").format(new Date())+"- "+message);
//    }
//}