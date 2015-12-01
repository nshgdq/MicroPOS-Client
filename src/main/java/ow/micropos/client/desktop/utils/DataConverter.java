package ow.micropos.client.desktop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import email.com.gmail.ttsai0509.gson.DateDeserializer;
import email.com.gmail.ttsai0509.gson.DateSerializer;
import email.com.gmail.ttsai0509.gson.ExposeDeserializationExclusionStrategy;
import email.com.gmail.ttsai0509.gson.ExposeSerializationExclusionStrategy;
import retrofit.converter.GsonConverter;
import retrofit.converter.JacksonConverter;

import java.util.Date;

public final class DataConverter {

    private DataConverter() {}

    public static GsonConverter gson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .addSerializationExclusionStrategy(new ExposeSerializationExclusionStrategy())
                .addDeserializationExclusionStrategy(new ExposeDeserializationExclusionStrategy())
                .create();
        return new GsonConverter(gson);
    }

    public static JacksonConverter jackson() {
        ObjectMapper mapper = new ObjectMapper();
        return new JacksonConverter(mapper);
    }

}
