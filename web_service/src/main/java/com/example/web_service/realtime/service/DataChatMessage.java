package com.example.web_service.realtime.service;

import com.example.web_service.realtime.model.ChatMessageModel;
import com.example.web_service.realtime.model.ChatMessageModelTranslate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class DataChatMessage {
    @Autowired
    private DiscoveryClient discoveryClient;
    private List<ChatMessageModel> chatMessageModelList = new ArrayList<>();
    private List<ChatMessageModelTranslate> chatMessageModelTransleteList = new ArrayList<>();


    @PostConstruct
    public void getData() {
        chatMessageModelList = getChatMessageModelList();
        translate(chatMessageModelList);
    }


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

    private List<ChatMessageModelTranslate> translate(List<ChatMessageModel> chatMessageModels){
        System.out.println("3");
        List<ServiceInstance> instances = discoveryClient.getInstances("translate-service");
        if (instances != null && instances.size() > 0) {//todo: replace with a load balancing mechanism
            ServiceInstance serviceInstance = instances.get(0);
            String url = serviceInstance.getUri().toString();

            String urlSave = url + "/translateList";
            RestTemplate restTemplate = new RestTemplate();

            Type listType = new TypeToken<ArrayList<ChatMessageModelTranslate>>(){}.getType();

            try {
                chatMessageModelTransleteList = new Gson().fromJson(
                        restTemplate.postForObject(urlSave, new Gson().toJson(chatMessageModels), String.class), listType);
            }catch (Exception e){
                e.printStackTrace();
                chatMessageModels.forEach(el->chatMessageModelTransleteList.add(new ChatMessageModelTranslate(el)));
                return chatMessageModelTransleteList;
            }
            return chatMessageModelTransleteList;
        }
        chatMessageModels.forEach(e->{
                chatMessageModelTransleteList.add(new ChatMessageModelTranslate(e));
            }
        );
        return chatMessageModelTransleteList;
    }


    private List<ChatMessageModel> getChatMessageModelList(){
        System.out.println("1");
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
