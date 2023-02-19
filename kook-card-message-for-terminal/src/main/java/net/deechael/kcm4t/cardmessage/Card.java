package net.deechael.kcm4t.cardmessage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.deechael.dutil.gson.JABuilder;
import net.deechael.dutil.gson.JOBuilder;
import net.deechael.kcm4t.cardmessage.module.Module;
import net.deechael.kcm4t.cardmessage.type.Size;
import net.deechael.kcm4t.cardmessage.type.Theme;

import java.util.ArrayList;
import java.util.List;

public class Card implements Base {

    private final Theme theme = Theme.PRIMARY;
    private final Size size = Size.LG;

    private final List<Module> modules = new ArrayList<>();

    public Card append(Module module) {
        this.modules.add(module);
        return this;
    }

    @Override
    public JsonObject serialize() {
        JABuilder<? extends JOBuilder<?>> builder = JOBuilder.of()
                .string("type", "card")
                .string("theme", this.theme.name().toLowerCase(), "primary")
                .string("size", this.size.name().toLowerCase(), "lg")
                .array("modules");
        this.modules.stream().map(Base::serialize)
                .map(JsonElement::getAsJsonObject)
                .forEach(builder::object);
        return builder.done().build();
    }

    public static Card deserialize(JsonElement element) {
        Card card = new Card();
        if (element.isJsonObject()) {

        }
        return card;
    }

}
