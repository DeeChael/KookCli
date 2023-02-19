package net.deechael.kcm4t.cardmessage.element;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.deechael.dutil.gson.JOReader;

import java.lang.reflect.Type;

public class Button implements Element {
    @Override
    public JsonElement serialize() {
        return null;
    }

    public static class Deserializer implements JsonDeserializer<Button> {

        public final static Deserializer INSTANCE = new Deserializer();

        @Override
        public Button deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("Should be a json object");
            JOReader reader = new JOReader(json.getAsJsonObject());
            if (!reader.string("type").equals("button"))
                throw new JsonParseException("Not a button");
            return null;
        }

    }

}
