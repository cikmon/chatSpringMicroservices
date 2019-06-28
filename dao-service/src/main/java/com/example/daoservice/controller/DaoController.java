package com.example.daoservice.controller;

import com.example.daoservice.model.ChatMessageModel;
import com.example.daoservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DaoController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/getAllMessages")
    public List<ChatMessageModel> getAll(){
        System.out.println(chatMessageRepository.findAll().toString());
        return chatMessageRepository.findAll(); }

    @PostMapping("/addMessage")
    public ChatMessageModel addMassage(@RequestBody ChatMessageModel chatMessageModel){
        if(chatMessageModel.getText()=="" ) return null;
        return chatMessageRepository.save(chatMessageModel);
    }

}
