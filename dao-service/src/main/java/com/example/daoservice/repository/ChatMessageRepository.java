package com.example.daoservice.repository;

import com.example.daoservice.model.ChatMessageModel;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessageModel, String> {
    List<ChatMessageModel> findAllByOrderByCreateDateAsc();
}
