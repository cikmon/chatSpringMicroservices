package com.example.web_service.realtime.controller;

import com.example.web_service.realtime.model.ChatMessageModel;
import com.example.web_service.realtime.model.ChatMessageModelTranslate;
import com.example.web_service.realtime.service.DataChatMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Controller
public class WebClientController {
    @Autowired
    private DataChatMessage dataChatMessage;

    @GetMapping("/")
    public String handleRequest(Model model) {
        return "chat";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/chat")
    public String chat(Model model) { return "chat"; }

    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    @MessageMapping("/newMessage")
    @SendTo("/topic/newMessage")
    public String save(ChatMessageModel chatMessageModel) {
        return dataChatMessage.save(chatMessageModel);
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public HttpEntity list() {
        return new ResponseEntity(dataChatMessage.getChatMessageModelTransleteList(), HttpStatus.OK);
    }

}
