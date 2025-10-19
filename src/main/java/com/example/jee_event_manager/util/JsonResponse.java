package com.example.jee_event_manager.util;

import java.util.HashMap;
import java.util.Map;

public class JsonResponse {
    
    private boolean success;
    private String message;
    private Object data;
    private Map<String, Object> metadata;
    
    public JsonResponse() {
        this.metadata = new HashMap<>();
    }
    
    public JsonResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public JsonResponse(boolean success, String message, Object data) {
        this(success, message);
        this.data = data;
    }
    
    // Méthodes statiques pour créer des réponses
    public static JsonResponse success(String message) {
        return new JsonResponse(true, message);
    }
    
    public static JsonResponse success(String message, Object data) {
        return new JsonResponse(true, message, data);
    }
    
    public static JsonResponse error(String message) {
        return new JsonResponse(false, message);
    }
    
    public static JsonResponse error(String message, Object data) {
        return new JsonResponse(false, message, data);
    }
    
    // Ajouter des métadonnées
    public JsonResponse addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
    
    // Getters et Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
