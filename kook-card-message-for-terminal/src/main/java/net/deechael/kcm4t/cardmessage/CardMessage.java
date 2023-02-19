package net.deechael.kcm4t.cardmessage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.deechael.dutil.gson.JABuilder;

import java.util.ArrayList;
import java.util.List;

public class CardMessage implements Base {

    private final List<Card> cards = new ArrayList<>();

    @Override
    public JsonArray serialize() {
        JABuilder<?> builder = new JABuilder<>(null);
        cards.stream().map(Base::serialize)
                .map(JsonElement::getAsJsonObject)
                .forEach(builder::object);
        return builder.build();
    }

    public static CardMessage deserialize(JsonElement element) {
        CardMessage cardMessage = new CardMessage();
        if (element.isJsonArray()) {

        }
        return cardMessage;
    }

}
