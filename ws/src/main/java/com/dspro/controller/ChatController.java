package com.dspro.controller;

import com.dspro.dto.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

//@CrossOrigin("*")
@Controller
@ComponentScan()
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    SimpMessagingTemplate template;

    @SubscribeMapping("/gg/wp")
    public ChatMessage simple() {
        return new ChatMessage("wada", "dd");
    }

    @MessageMapping("/for/{username}")
    @SendTo("/for/{username}")//dest chanel
    public ChatMessage send(ChatMessage message, @AuthenticationPrincipal UserDetails details, Principal principal,
                            @DestinationVariable("username") String username, @CurrentUser User user) throws Exception {
        log.warn("principal {}", principal);
        log.warn("username {}", username);
        log.warn("details {}", details);
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new ChatMessage("waaah", time);
    }


}
