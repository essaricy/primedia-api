package com.sednar.digital.media.resource.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebSocketResource {

    private SimpMessagingTemplate template;

    @Autowired
    WebSocketResource(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/hello1")
    @SendTo("/topic/greetings")
    public String greeting(String date) throws Exception {
        return date + ": Hello World 1";
    }

    @PostMapping("/hello2")
    public @ResponseBody void greet(String date) {
        this.template.convertAndSend("/topic/greetings", date + ": Hello World 2");
    }

}
