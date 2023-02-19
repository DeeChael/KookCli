package net.deechael.kcm4t.cardmessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.deechael.kcm4t.cardmessage.element.Button;
import net.deechael.kcm4t.cardmessage.element.Text;
import net.deechael.kcm4t.cardmessage.module.Section;
import net.deechael.kcm4t.cardmessage.struct.Paragraph;

import java.util.HashMap;
import java.util.Map;

public class Deserializer {

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Text.class, Text.Deserializer.INSTANCE)
            .registerTypeAdapter(Button.class, Button.Deserializer.INSTANCE)
            .registerTypeAdapter(Section.class, Section.Deserializer.INSTANCE)
            .create();

    private final static Map<String, Class<? extends Base>> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("card", Card.class);
        TYPE_MAP.put("plain-text", Text.class);
        TYPE_MAP.put("kmarkdown", Text.class);
        TYPE_MAP.put("paragraph", Paragraph.class);
        TYPE_MAP.put("section", Section.class);
        TYPE_MAP.put("button", Button.class);
    }

    public static Class<?> getType(String type) {
        if (!TYPE_MAP.containsKey(type))
            throw new RuntimeException("The type is not exists");
        return TYPE_MAP.get(type);
    }

}
