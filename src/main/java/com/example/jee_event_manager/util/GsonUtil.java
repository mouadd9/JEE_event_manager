package com.example.jee_event_manager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

public class GsonUtil {
    
    private static final Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();
  
    public static Gson getGson() {
        return GSON_INSTANCE;
    }
    
    
    public static String toJson(Object obj) {
        return GSON_INSTANCE.toJson(obj);
    }
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON_INSTANCE.fromJson(json, classOfT);
    }
}
