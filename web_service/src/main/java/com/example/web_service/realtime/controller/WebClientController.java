package com.example.web_service.realtime.controller;

import com.example.web_service.realtime.model.ChatMessageModel;
import com.example.web_service.realtime.model.ChatMessageModelTranslate;
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
    private DiscoveryClient discoveryClient;

    private  List<ChatMessageModel> chatMessageModelList;
    private List<ChatMessageModelTranslate> chatMessageModelTransleteList;


    @PostConstruct
    public void getData() {
        chatMessageModelList = getChatMessageModelList();
        chatMessageModelTransleteList=translate(chatMessageModelList);

    }

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
            if(chatMessageModel.getText()!="" || chatMessageModel.getText()!=null) {
                List<ServiceInstance> instances = discoveryClient.getInstances("dao-service");
                if (instances != null && instances.size() > 0) {//todo: replace with a load balancing mechanism
                    ServiceInstance serviceInstance = instances.get(0);
                    String url = serviceInstance.getUri().toString();
                    String urlSave = url + "/addMessage";

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.postForObject(urlSave, chatMessageModel, String.class);

                    ChatMessageModelTranslate chatMessageModelTranslateTmp=new ChatMessageModelTranslate(chatMessageModel);
                    chatMessageModelTranslateTmp.setTranslate(translate(chatMessageModel.getText()));
                    chatMessageModelTransleteList.add(chatMessageModelTranslateTmp);
                }
            }
        return new Gson().toJson(chatMessageModelTransleteList);
    }


    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public HttpEntity list() {

        return new ResponseEntity(chatMessageModelTransleteList, HttpStatus.OK);
    }




    private List<ChatMessageModelTranslate> translate(List<ChatMessageModel> chatMessageModels){
        List<ChatMessageModelTranslate> chatMessageModelsTranslete= new ArrayList<>();

        List<ServiceInstance> instances = discoveryClient.getInstances("translate-service");
        if (instances != null && instances.size() > 0) {//todo: replace with a load balancing mechanism
            ServiceInstance serviceInstance = instances.get(0);
            String url = serviceInstance.getUri().toString();

            String urlSave = url + "/translateList";
            RestTemplate restTemplate = new RestTemplate();

            Type listType = new TypeToken<ArrayList<ChatMessageModelTranslate>>(){}.getType();

            try {
                chatMessageModelsTranslete = new Gson().fromJson(
                        restTemplate.postForObject(urlSave, new Gson().toJson(chatMessageModels), String.class), listType);
            }catch (Exception e){
                e.printStackTrace();
                chatMessageModels.forEach(el->chatMessageModelTransleteList.add(new ChatMessageModelTranslate(el)));
                return chatMessageModelTransleteList;
            }

            return chatMessageModelsTranslete;
        }

        chatMessageModels.forEach(e->chatMessageModelTransleteList.add(new ChatMessageModelTranslate(e)));
        return chatMessageModelTransleteList;
    }


    private String translate(String text){
        List<ServiceInstance> instances = discoveryClient.getInstances("translate-service");
        if (instances != null && instances.size() > 0) {//todo: replace with a load balancing mechanism
            ServiceInstance serviceInstance = instances.get(0);
            String url = serviceInstance.getUri().toString();

            String urlSave = url + "/translate";
            RestTemplate restTemplate = new RestTemplate();
            try {
                return restTemplate.postForObject(urlSave, text, String.class);
            }catch(Exception e){
                return "";
            }
        }

        return "";
    }

    private List<ChatMessageModel> getChatMessageModelList(){
        List<ChatMessageModel> chatMessageModels = null;

        List<ServiceInstance> instances = discoveryClient.getInstances("dao-service");
        if (instances != null && instances.size() > 0) {//todo: replace with a load balancing mechanism
            ServiceInstance serviceInstance = instances.get(0);
            String url = serviceInstance.getUri().toString();
            url = url + "/getAllMessages";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<ChatMessageModel>> response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ChatMessageModel>>(){});
            chatMessageModels = response.getBody();
        }

        return chatMessageModels;
    }




}
