package net.deechael.kcm4t.cardmessage.module;

import com.google.gson.*;
import net.deechael.dutil.gson.JOBuilder;
import net.deechael.dutil.gson.JOReader;
import net.deechael.kcm4t.cardmessage.Base;
import net.deechael.kcm4t.cardmessage.element.Button;
import net.deechael.kcm4t.cardmessage.element.Image;
import net.deechael.kcm4t.cardmessage.element.Text;
import net.deechael.kcm4t.cardmessage.struct.Paragraph;
import net.deechael.kcm4t.cardmessage.type.SectionMode;
import net.deechael.kcm4t.utils.Container;

import java.lang.reflect.Type;

public class Section implements Module {

    private final Container<Base> text = new Container<Base>()
            .accessible(Text.class)
            .accessible(Paragraph.class)
            .withDefault(new Text());

    private SectionMode mode = SectionMode.NONE;

    private final Container<Base> accessory = new Container<Base>()
            .accessible(Button.class)
            .accessible(Image.class);

    public Section text(Text text) {
        if (text == null)
            return this;
        this.text.setValue(text);
        return this;
    }

    public Section text(Paragraph text) {
        if (text == null)
            return this;
        this.text.setValue(text);
        return this;
    }

    public Section mode(SectionMode mode) {
        if (mode == null)
            return this;
        this.mode = mode;
        return this;
    }

    @Override
    public JsonObject serialize() {
        return JOBuilder.of()
                .string("type", "section")
                .object("text", this.text.getValue().serialize().getAsJsonObject(), new Text().serialize())
                .string("mode", this.mode.name().toLowerCase())
                .build();
    }

    public static class Deserializer implements JsonDeserializer<Section> {

        public final static Deserializer INSTANCE = new Deserializer();

        @Override
        public Section deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("Should be a json object");
            JOReader reader = new JOReader(json.getAsJsonObject());
            if (!reader.string("type").equals("section"))
                throw new JsonParseException("Not a section");

            return null;
        }

    }

}
