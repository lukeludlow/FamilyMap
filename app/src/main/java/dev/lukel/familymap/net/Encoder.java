package dev.lukel.familymap.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Encoder {
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }
    public static <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
