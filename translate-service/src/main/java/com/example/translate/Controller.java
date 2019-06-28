package com.example.translate;

import com.example.translate.model.ChatMessageModel;
import com.example.translate.model.ChatMessageModelTranslate;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.*;

import com.darkprograms.speech.translator.GoogleTranslate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
public class Controller {


@PostMapping("/translateList")
public List<ChatMessageModelTranslate> getTranslateList(@RequestBody String chatMessageModelsArr){
    Type listType = new TypeToken<ArrayList<ChatMessageModel>>(){}.getType();
    List<ChatMessageModel> chatMessageModelsList = new Gson().fromJson(chatMessageModelsArr, listType);

    List<ChatMessageModelTranslate> chatMessageModelTranslateList= new ArrayList<>();
    chatMessageModelsList.forEach(e-> chatMessageModelTranslateList.add(new ChatMessageModelTranslate(e)));

    chatMessageModelTranslateList.forEach(e-> {
        if(e.getText() != "") {
            try {
                e.setTranslate(GoogleTranslate.translate("ru", e.getText()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            chatMessageModelTranslateList.set(chatMessageModelTranslateList.indexOf(e), e);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    } ) ;


    return chatMessageModelTranslateList;
}

    @PostMapping("/translate")
    public String getTranslateText(@RequestBody String text) {
        String translete="";
        try {
            translete=GoogleTranslate.translate("ru", text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return translete;
    }
}
