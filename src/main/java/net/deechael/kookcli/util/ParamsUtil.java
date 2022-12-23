package net.deechael.kookcli.util;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ParamsUtil {

    @NotNull
    public static Map<String, String> params(@NotNull Map.Entry<String, String>... entries) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String> entry : entries) {
            params.put(entry.getKey(), entry.getValue());
        }
        return params;
    }

    @NotNull
    public static Map.Entry<String, String> param(@NotNull String key, @NotNull String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static JsonObject json(@NotNull Map.Entry<String, String>... entries) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, String> entry : entries) {
            jsonObject.addProperty(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }

}
