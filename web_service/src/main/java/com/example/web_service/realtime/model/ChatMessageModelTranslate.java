package com.example.web_service.realtime.model;

import java.util.Date;

public class ChatMessageModelTranslate extends ChatMessageModel {
    private String translate="3333333";

   public ChatMessageModelTranslate(ChatMessageModel chatMessageModel){
       super.id = chatMessageModel.id;

       if(chatMessageModel.text==""||chatMessageModel.text==null||chatMessageModel.text.trim().isEmpty() ) {
           super.text = "";
       }else{ super.text = chatMessageModel.text;}

       super.author = chatMessageModel.author;
       super.createDate = chatMessageModel.createDate;



    }



    public String getTranslate() {
        return translate;
    }
    public void setTranslate(String translate) {
        this.translate = translate;
    }
    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + super.id + '\"' +
                ",\"text\":\"" + super.text + '\"' +
                ",\"author\":\"" + super.author + '\"' +
                ",\"createDate\":\"" + super.createDate + "\"" +
                ",\"translate\":\"" + this.translate + "\"" +
                '}';
    }
}
