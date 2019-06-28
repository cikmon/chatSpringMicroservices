package com.example.translate.model;

public class ChatMessageModelTranslate extends ChatMessageModel {
    private String translate;

   public ChatMessageModelTranslate(ChatMessageModel chatMessageModel){
       super.id = chatMessageModel.id;
       super.text=chatMessageModel.text;
       super.author = chatMessageModel.author;
       super.createDate = chatMessageModel.createDate;
    }
    public ChatMessageModelTranslate(){}


    public String getTranslate() {
        return translate;
    }
    public void setTranslate(String translate) {
        this.translate = translate;
    }
    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ",\"text\":\"" + text + '\"' +
                ",\"author\":\"" + author + '\"' +
                ",\"createDate\":\"" + createDate + "\"" +
                ",\"translate\":\"" + translate + "\"" +
                '}';
    }
}
