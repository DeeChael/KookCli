package net.deechael.kcm4t.cardmessage.element;

import com.google.gson.*;
import net.deechael.dutil.gson.JOBuilder;
import net.deechael.dutil.gson.JOReader;
import net.deechael.kcm4t.cardmessage.type.TextType;

import java.lang.reflect.Type;

public class Text implements Element {

    private TextType type = TextType.PLAIN_TEXT;
    private String content = "";

    public Text type(TextType type) {
        if (type == null)
            return this;
        this.type = type;
        return this;
    }

    public Text content(String content) {
        if (content == null)
            return this;
        this.content = content;
        return this;
    }

    @Override
    public JsonObject serialize() {
        return JOBuilder.of()
                .string("type", type.value(), "plain-text")
                .string("content", this.content, "")
                .build();
    }

    public static class Deserializer implements JsonDeserializer<Text> {

        public final static Deserializer INSTANCE = new Deserializer();

        @Override
        public Text deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("Should be a json object");
            JOReader reader = new JOReader(json.getAsJsonObject());
            if (!reader.string("type").equals("plain-text") && !reader.string("type").equals("kmarkdown"))
                throw new JsonParseException("Not a text");
            Text text = new Text()
                    .type(reader.string("type").equals("kmarkdown") ? TextType.KMARKDOWN : TextType.PLAIN_TEXT);
            return text.content(reader.string("content"));
        }

    }

}
