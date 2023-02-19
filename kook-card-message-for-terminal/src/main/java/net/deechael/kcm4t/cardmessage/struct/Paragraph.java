package net.deechael.kcm4t.cardmessage.struct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.deechael.dutil.gson.JABuilder;
import net.deechael.dutil.gson.JOBuilder;
import net.deechael.kcm4t.cardmessage.Base;
import net.deechael.kcm4t.cardmessage.element.Text;

import java.util.ArrayList;
import java.util.List;

public class Paragraph implements Struct {

    private int cols = 3;
    private final List<Text> fields = new ArrayList<>();

    public Paragraph columns(int cols) {
        this.cols = cols;
        return this;
    }

    public Paragraph append(Text text) {
        if (text == null)
            return this;
        this.fields.add(text);
        return this;
    }

    @Override
    public JsonObject serialize() {
        JABuilder<? extends JOBuilder<?>> builder = JOBuilder.of()
                .string("type", "paragraph")
                .number("cols", this.cols, number -> {
                    int value = number.intValue();
                    return value >= 1 && value <= 3;
                }, 3)
                .array("fields");
        this.fields.stream().map(Base::serialize)
                .map(JsonElement::getAsJsonObject)
                .forEach(builder::object);
        return builder.done().build();
    }

}
